const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

export async function apiRequest<TResp>(
    path: string,
    method: HttpMethod,
    body?: unknown
): Promise<TResp> {
    const res = await fetch(`${BASE_URL}${path}`, {
        method,
        headers: {
            "Content-Type": "application/json"
        },
        body: body === undefined ? undefined : JSON.stringify(body)
    });

    if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(`HTTP ${res.status} ${res.statusText}: ${text}`);
    }

    // allow empty responses
    const contentType = res.headers.get("content-type") || "";
    if (!contentType.includes("application/json")) {
        return undefined as unknown as TResp;
    }
    return (await res.json()) as TResp;
}
