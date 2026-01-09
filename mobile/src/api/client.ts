export const API_BASE_URL = "http://34.118.98.3";

type Json = Record<string, any> | any[];

/**
 * Minimal fetch wrapper:
 * - Throws for non-2xx
 * - Parses JSON
 * - Produces readable error messages
 */
export async function getJson<T>(path: string): Promise<T> {
  const url = `${API_BASE_URL}${path}`;

  let res: Response;
  try {
    res = await fetch(url, {
      method: "GET",
      headers: { Accept: "application/json" },
    });
  } catch (e: any) {
    throw new Error(`Network error calling ${url}: ${e?.message ?? String(e)}`);
  }

  const text = await res.text();
  const data = text ? (JSON.parse(text) as Json) : null;

  if (!res.ok) {
    const message =
      (data && typeof data === "object" && "message" in data && (data as any).message) ||
      `HTTP ${res.status} ${res.statusText}`;
    throw new Error(message);
  }

  return data as T;
}

/**
 * POST request wrapper:
 * - Sends JSON body
 * - Throws for non-2xx
 * - Parses JSON response
 * - Produces readable error messages
 */
export async function postJson<T>(path: string, body: Record<string, any>): Promise<T> {
  const url = `${API_BASE_URL}${path}`;

  let res: Response;
  try {
    res = await fetch(url, {
      method: "POST",
      headers: {
        "Accept": "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });
  } catch (e: any) {
    throw new Error(`Network error calling ${url}: ${e?.message ?? String(e)}`);
  }

  const text = await res.text();
  const data = text ? (JSON.parse(text) as Json) : null;

  if (!res.ok) {
    const message =
      (data && typeof data === "object" && "message" in data && (data as any).message) ||
      `HTTP ${res.status} ${res.statusText}`;
    throw new Error(message);
  }

  return data as T;
}
