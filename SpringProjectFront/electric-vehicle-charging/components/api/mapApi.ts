import { api } from "./axios"

export async function fetchStations() {
  const res = await api.get("/api/stations")
  return res.data
}