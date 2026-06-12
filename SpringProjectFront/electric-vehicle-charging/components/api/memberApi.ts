import { api } from "./axios"

const TOKEN_KEY = "volt_access_token"

function authHeaders() {
  const token = localStorage.getItem(TOKEN_KEY)
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export async function verifyMember(userId: string, password: string) {
  const res = await api.post("/api/members/verify", { userId, password }, { headers: authHeaders() })
  return res.data as boolean
}

export async function updateMemberName(userId: string, name: string) {
  return api.put("/api/members/name", { userId, name }, { headers: authHeaders() })
}

export async function updateMemberEmail(userId: string, email: string) {
  return api.put("/api/members/email", { userId, email }, { headers: authHeaders() })
}

export async function updateMemberPassword(userId: string, password: string) {
  return api.put("/api/members/password", { userId, password }, { headers: authHeaders() })
}

export async function deleteMember(id: string) {
  return api.delete(`/api/members/${id}`, { headers: authHeaders() })
}