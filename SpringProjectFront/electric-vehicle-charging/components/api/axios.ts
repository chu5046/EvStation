import axios from "axios"

const TOKEN_KEY = "volt_access_token"

export const api = axios.create({
  baseURL:  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080",
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})