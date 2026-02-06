/* eslint-disable no-console */

const fs = require("fs");
const path = require("path");
const { execSync } = require("child_process");
const {
    getProjectMeta,
    ensureItemInProject,
    setSingleSelectField,
} = require("./projectv2");

/* =========================================================
 * ENV
 * ======================================================= */

const [OWNER, REPO] = (process.env.GITHUB_REPOSITORY || "").split("/");

const GH_TOKEN = process.env.GITHUB_TOKEN;          // Issues / labels
const PROJECT_TOKEN = process.env.PROJECT_TOKEN;   // Project v2 (PAT / App)

const DEFAULT_BRANCH = process.env.DEFAULT_BRANCH || "main";
const SYNC_MODE = (process.env.SYNC_MODE || "changed").toLowerCase(); // changed | full

const PROJECT_KIND = process.env.PROJECT_KIND || "user"; // user | organization
const PROJECT_OWNER = process.env.PROJECT_OWNER;
const PROJECT_NUMBER = process.env.PROJECT_NUMBER;

const FIELD_STATUS = process.env.PROJECT_FIELD_STATUS || "Status";
const FIELD_AREA = process.env.PROJECT_FIELD_AREA || "Area";

if (!OWNER || !REPO) {
    console.error("Missing GITHUB_REPOSITORY");
    process.exit(1);
}
if (!GH_TOKEN) {
    console.error("Missing GITHUB_TOKEN");
    process.exit(1);
}

/* =========================================================
 * HELPERS
 * ======================================================= */

function sh(cmd) {
    return execSync(cmd, { stdio: ["ignore", "pipe", "pipe"] })
        .toString("utf8")
        .trim();
}

async function ghRest(pathname, { method = "GET", body, token = GH_TOKEN } = {}) {
    const url = `https://api.github.com${pathname}`;
    const headers = {
        Accept: "application/vnd.github+json",
        Authorization: `Bearer ${token}`,
        "X-GitHub-Api-Version": "2022-11-28",
    };

    const r = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    const text = await r.text();
    if (!r.ok) {
        // Print API error payload (usually JSON), then fail
        console.error(text);
        throw new Error(`GitHub REST ${method} ${pathname} failed: ${r.status}`);
    }

    return text ? JSON.parse(text) : null;
}

/* =========================================================
 * DOMAIN LOGIC
 * ======================================================= */

const AREAS = [
    { prefix: "work/bugs/", areaLabel: "area:bugs", areaValue: "bugs" },
    { prefix: "work/features/", areaLabel: "area:features", areaValue: "features" },
    { prefix: "work/tasks/", areaLabel: "area:tasks", areaValue: "tasks" },
];

function parseTicket(filePath) {
    const text = fs.readFileSync(filePath, "utf8");

    const h1 = text.match(/^#\s+(.+)$/m)?.[1]?.trim();
    if (!h1) return null;

    const id =
        path.basename(filePath).match(/^([A-Z]+-\d+)/)?.[1] ||
        h1.match(/^([A-Z]+-\d+)/)?.[1];

    if (!id) return null;

    const status = text.match(/^[*-]\s+Status:\s*(.+)$/mi)?.[1]?.trim() || null;

    const area = AREAS.find(a => filePath.startsWith(a.prefix));

    return {
        id,
        title: h1,
        status,
        areaLabel: area?.areaLabel || null,
        areaValue: area?.areaValue || null,
        filePath,
    };
}

async function ensureLabel(name, color = "ededed") {
    try {
        await ghRest(`/repos/${OWNER}/${REPO}/labels/${encodeURIComponent(name)}`, { method: "GET" });
    } catch {
        await ghRest(`/repos/${OWNER}/${REPO}/labels`, {
            method: "POST",
            body: { name, color },
        });
    }
}

function sleep(ms) {
    return new Promise(r => setTimeout(r, ms));
}

async function resolveIssueNodeWithRetry(token, contentNodeId) {
    const findQuery = `
    query($id:ID!) {
      node(id:$id) {
        ... on Issue {
          id
          projectItems(first: 50) {
            nodes { id project { id } }
          }
        }
      }
    }
  `;

    const delays = [1000, 2000, 4000, 8000, 8000]; // ~23s max
    let lastErr = null;

    for (let i = 0; i < delays.length; i++) {
        try {
            const data = await gql(token, findQuery, { id: contentNodeId });
            if (data?.node) return data; // success
            lastErr = new Error("node is null");
        } catch (e) {
            lastErr = e;
        }
        await sleep(delays[i]);
    }

    throw lastErr || new Error("Could not resolve node after retries");
}

/* =========================================================
 * FILE SELECTION
 * ======================================================= */

function collectFiles() {
    if (SYNC_MODE === "full") {
        console.log("Running FULL sync (bootstrap mode)");
        // simplest + robust; no parentheses, no multiline
        const out = sh(`find work/bugs work/features work/tasks -type f 2>/dev/null || true`);
        return out ? out.split("\n").filter(Boolean) : [];
    }

    console.log("Running CHANGED sync");
    // works for PR and non-PR; use origin/default...HEAD
    sh(`git fetch origin ${DEFAULT_BRANCH} --depth=1 || true`);

    const out = sh(`git diff --name-only origin/${DEFAULT_BRANCH}...HEAD || true`);
    const files = out ? out.split("\n").filter(Boolean) : [];

    return files.filter(p =>
        p.startsWith("work/bugs/") ||
        p.startsWith("work/features/") ||
        p.startsWith("work/tasks/")
    );
}

/* =========================================================
 * MAIN
 * ======================================================= */

async function syncToProject(projectMeta, issueNodeId, statusName, areaName) {
    if (!projectMeta || !PROJECT_TOKEN || !issueNodeId) return;

    const itemId = await ensureItemInProject({
        token: PROJECT_TOKEN,
        projectId: projectMeta.projectId,
        contentNodeId: issueNodeId,
    });

    // Status field
    if (projectMeta.statusFieldId && statusName) {
        const optId = projectMeta.statusOptions.get(statusName);
        if (optId) {
            await setSingleSelectField({
                token: PROJECT_TOKEN,
                projectId: projectMeta.projectId,
                itemId,
                fieldId: projectMeta.statusFieldId,
                optionId: optId,
            });
        } else {
            console.warn(`[ProjectV2] Status option not found: "${statusName}"`);
        }
    }

    // Area field
    if (projectMeta.areaFieldId && areaName) {
        const optId = projectMeta.areaOptions.get(areaName);
        if (optId) {
            await setSingleSelectField({
                token: PROJECT_TOKEN,
                projectId: projectMeta.projectId,
                itemId,
                fieldId: projectMeta.areaFieldId,
                optionId: optId,
            });
        } else {
            console.warn(`[ProjectV2] Area option not found: "${areaName}"`);
        }
    }
}

async function main() {
    const files = collectFiles();
    if (files.length === 0) {
        console.log("No ticket files to process.");
        return;
    }

    // base labels
    for (const base of ["tickets", "area:bugs", "area:features", "area:tasks"]) {
        await ensureLabel(base);
    }

    // project meta loaded once
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

    for (const file of files) {
        if (!fs.existsSync(file) || fs.statSync(file).isDirectory()) continue;

        const ticket = parseTicket(file);
        if (!ticket) {
            console.log(`Skip (cannot parse): ${file}`);
            continue;
        }

        const ticketLabel = `ticket:${ticket.id}`;
        await ensureLabel(ticketLabel);

        const statusLabel = ticket.status ? `status:${ticket.status}` : null;
        if (statusLabel) await ensureLabel(statusLabel);

        // find existing issue by ticket label
        const q = encodeURIComponent(`repo:${OWNER}/${REPO} is:issue label:"${ticketLabel}"`);
        const search = await ghRest(`/search/issues?q=${q}`, { method: "GET" });
        const existing = search.items?.[0] || null;

        // link always points to DEFAULT_BRANCH as requested
        const link = `https://github.com/${OWNER}/${REPO}/blob/${DEFAULT_BRANCH}/${ticket.filePath}`;
        const body = `Ticket je veden v repozitáři.\n\n➡ ${link}\n`;

        let issue;

        if (!existing) {
            issue = await ghRest(`/repos/${OWNER}/${REPO}/issues`, {
                method: "POST",
                body: {
                    title: ticket.title,
                    body,
                    labels: [
                        "tickets",
                        ticketLabel,
                        ticket.areaLabel,
                        statusLabel,
                    ].filter(Boolean),
                },
            });

            console.log(`Created issue #${issue.number} (${ticket.id})`);
        } else {
            // Update title/body
            issue = await ghRest(`/repos/${OWNER}/${REPO}/issues/${existing.number}`, {
                method: "PATCH",
                body: { title: ticket.title, body },
            });

            // Sync labels: keep everything except old status:*
            const oldLabels = (issue.labels || []).map(l => (typeof l === "string" ? l : l.name));
            const kept = oldLabels.filter(l => !l.startsWith("status:"));
            const next = Array.from(new Set([
                ...kept,
                "tickets",
                ticketLabel,
                ...(ticket.areaLabel ? [ticket.areaLabel] : []),
                ...(statusLabel ? [statusLabel] : []),
            ]));

            await ghRest(`/repos/${OWNER}/${REPO}/issues/${issue.number}/labels`, {
                method: "PUT",
                body: next,
            });

            console.log(`Updated issue #${issue.number} (${ticket.id})`);
        }

        // Always re-fetch canonical issue object (guarantees node_id is real Issue node)
        const canonical = await ghRest(`/repos/${OWNER}/${REPO}/issues/${issue.number}`, { method: "GET" });
        console.log(`Project sync for ${ticket.id}: issue #${issue.number}, node_id=${canonical?.node_id}`);

        // sync into Project v2
        if (projectMeta && canonical?.node_id) {
            await syncToProject(projectMeta, canonical.node_id, ticket.status, ticket.areaValue);
        }
    }
}

main().catch(err => {
    console.error(err);
    process.exit(1);
});
