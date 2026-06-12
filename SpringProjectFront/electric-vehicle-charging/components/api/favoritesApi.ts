import { api } from "./axios"

const TOKEN_KEY = "volt_access_token"

export interface FavoritePayload {
  userId: string
  stationId: string
}

function authHeaders() {
  try {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) return { Authorization: `Bearer ${token}` }
  } catch {
    // ignore
  }
  return {}
}

export async function getFavorites(userId: string): Promise<string[]> {
  try {
    console.log("getFavorites: requesting for userId:", userId)
    const res = await api.get<any[]>(`/api/favorites/${encodeURIComponent(userId)}`, {
      headers: authHeaders(),
    })
    console.log("getFavorites: response data:", res.data)
    const data = res.data || []
    if (!Array.isArray(data)) return []
    if (data.length === 0) return []
    // backend may return full station objects or just ids
    if (typeof data[0] === "string") return data as string[]
    if (typeof data[0] === "object" && data[0] !== null && "id" in data[0]) {
      return data.map((d) => (d && d.id ? String(d.id) : null)).filter(Boolean) as string[]
    }
    return []
  } catch (err) {
    console.error("getFavorites: error:", err)
    throw err
  }
}

export async function addFavorite(payload: FavoritePayload) {
  const res = await api.post(`/api/favorites`, payload, { headers: authHeaders() })
  return res.data
}

export async function removeFavorite(userId: string, stationId: string) {
  const res = await api.delete(`/api/favorites`, {
    params: { userId, stationId },
    headers: authHeaders(),
  })
  return res.data
}
