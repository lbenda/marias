/* eslint-disable no-console */
const fs = require("fs");
const { execSync } = require("child_process");
const { getProjectMeta, ensureItemInProject, setSingleSelectField } = require("./projectv2");

const OWNER = process.env.GITHUB_REPOSITORY?.split("/")[0];
const REPO = process.env.GITHUB_REPOSITORY?.split("/")[1];

const GH_TOKEN = process.env.GITHUB_TOKEN;
const PROJECT_TOKEN = process.env.PROJECT_TOKEN;

const DEFAULT_BRANCH = process.env.DEFAULT_BRANCH || "master";
const PR_NUMBER = process.env.PR_NUMBER;

const PROJECT_KIND = process.env.PROJECT_KIND || "organization";
const PROJECT_OWNER = process.env.PROJECT_OWNER;
const PROJECT_NUMBER = process.env.PROJECT_NUMBER;

const FIELD_STATUS = process.env.PROJECT_FIELD_STATUS || "Status";
const FIELD_AREA = process.env.PROJECT_FIELD_AREA || "Area";

if (!OWNER || !REPO || !GH_TOKEN || !PR_NUMBER) {
    console.error("Missing env: GITHUB_REPOSITORY, GITHUB_TOKEN, PR_NUMBER");
    process.exit(1);
}

function sh(cmd) {
    return execSync(cmd, { stdio: ["ignore", "pipe", "pipe"] }).toString("utf8").trim();
}

function rest(path, { method = "GET", body, token = GH_TOKEN } = {}) {
    const url = `https://api.github.com${path}`;
    const headers = {
        Accept: "application/vnd.github+json",
        Authorization: `Bearer ${token}`,
        "X-GitHub-Api-Version": "2022-11-28",
    };
    const payload = {
        method,
        headers,
        ...(body ? { body: JSON.stringify(body) } : {}),
    };

    return execSync(
        `node -e ${JSON.stringify(`
      (async () => {
        const r = await fetch(${JSON.stringify(url)}, ${JSON.stringify(payload)});
        const t = await r.text();
        if (!r.ok) { console.error(t); process.exit(2); }
        console.log(t);
      })();
    `)}`,
        { stdio: ["ignore", "pipe", "pipe"] }
    ).toString("utf8");
}

function restJson(path, opts) {
    const txt = rest(path, opts);
    return txt ? JSON.parse(txt) : null;
}

function ensureLabel(name, color = "ededed") {
    try {
        restJson(`/repos/${OWNER}/${REPO}/labels/${encodeURIComponent(name)}`, { method: "GET" });
    } catch {
        restJson(`/repos/${OWNER}/${REPO}/labels`, { method: "POST", body: { name, color } });
    }
}

function extractTicketIdFromPath(p) {
    return p.match(/\/([A-Z]+-\d+)\b/)?.[1] || null;
}

function setStatusMergedInFile(filePath) {
    const txt = fs.readFileSync(filePath, "utf8");
    const replaced = txt.replace(/^([\-\*]\s+Status:\s*)Done\s*$/mi, `$1Merged`);
    if (replaced !== txt) {
        fs.writeFileSync(filePath, replaced, "utf8");
        return true;
    }
    return false;
}

async function syncMergedToProject(projectMeta, issueNodeId) {
    if (!projectMeta?.statusFieldId) return;
    const opt = projectMeta.statusOptions.get("Merged");
    if (!opt) {
        console.warn(`[ProjectV2] Status option not found: "Merged"`);
        return;
    }
    const itemId = await ensureItemInProject({
        token: PROJECT_TOKEN,
        projectId: projectMeta.projectId,
        contentNodeId: issueNodeId,
    });
    await setSingleSelectField({
        token: PROJECT_TOKEN,
        projectId: projectMeta.projectId,
        itemId,
        fieldId: projectMeta.statusFieldId,
        optionId: opt,
    });
}

async function main() {
    // PR files
    const files = [];
    let page = 1;
    while (true) {
        const res = restJson(`/repos/${OWNER}/${REPO}/pulls/${PR_NUMBER}/files?per_page=100&page=${page}`);
        if (!Array.isArray(res) || res.length === 0) break;
        for (const f of res) files.push(f.filename);
        page += 1;
    }

    const watched = files.filter(p =>
        p.startsWith("work/bugs/") ||
        p.startsWith("work/features/") ||
        p.startsWith("work/tasks/")
    );

    if (watched.length === 0) {
        console.log("No ticket files in merged PR.");
        return;
    }

    ensureLabel("status:Merged");

    // Project meta
    let projectMeta = null;
    if (PROJECT_TOKEN && PROJECT_OWNER && PROJECT_NUMBER) {
        projectMeta = await getProjectMeta({
            token: PROJECT_TOKEN,
            kind: PROJECT_KIND,
            owner: PROJECT_OWNER,
            number: PROJECT_NUMBER,
            statusFieldName: FIELD_STATUS,
            areaFieldName: FIELD_AREA,
        });
    }

    const touchedForCommit = [];

    for (const p of watched) {
        const id = extractTicketIdFromPath(p);

        if (id) {
            const ticketLabel = `ticket:${id}`;
            const q = encodeURIComponent(`repo:${OWNER}/${REPO} is:issue label:"${ticketLabel}"`);
            const search = restJson(`/search/issues?q=${q}`, { method: "GET" });
            const found = (search.items || [])[0];

            if (found) {
                const issueNum = found.number;
                const current = restJson(`/repos/${OWNER}/${REPO}/issues/${issueNum}`, { method: "GET" });

                const oldLabels = (current.labels || []).map(l => (typeof l === "string" ? l : l.name));
                const kept = oldLabels.filter(l => !l.startsWith("status:"));
                const nextLabels = [...new Set([...kept, "status:Merged"])];

                // Close issue
                restJson(`/repos/${OWNER}/${REPO}/issues/${issueNum}`, {
                    method: "PATCH",
                    body: { state: "closed" },
                });
                // Update labels
                restJson(`/repos/${OWNER}/${REPO}/issues/${issueNum}/labels`, {
                    method: "PUT",
                    body: nextLabels,
                });

                console.log(`Closed issue #${issueNum} for ${id}`);

                // Project v2 -> Status = Merged
                if (projectMeta && PROJECT_TOKEN && current.node_id) {
                    await syncMergedToProject(projectMeta, current.node_id);
                }
            }
        }

        // Update file in master: Done -> Merged
        if (fs.existsSync(p) && fs.statSync(p).isFile()) {
            const changed = setStatusMergedInFile(p);
            if (changed) touchedForCommit.push(p);
        }
    }

    if (touchedForCommit.length > 0) {
        sh(`git config user.name "github-actions[bot]"`);
        sh(`git config user.email "github-actions[bot]@users.noreply.github.com"`);
        sh(`git add ${touchedForCommit.map(x => `"${x}"`).join(" ")}`);
        sh(`git commit -m "chore(tickets): mark merged tickets as Merged"`);
        sh(`git push origin ${DEFAULT_BRANCH}`);
        console.log(`Committed Merged status for ${touchedForCommit.length} file(s).`);
    } else {
        console.log("No files needed Status->Merged change.");
    }
}

main().catch(err => {
    console.error(err);
    process.exit(1);
});
