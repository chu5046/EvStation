"use client"

import { useEffect, useMemo, useState } from "react"
import dynamic from "next/dynamic"
import { fetchStations, type ChargingStation } from "@/lib/stations"
import { useAuth } from "./auth-context"
import { StationCard } from "./station-card"
import { cn } from "@/lib/utils"
import { Search, Star, List, MapIcon } from "lucide-react"

const MapView = dynamic(() => import("./map-view"), {
  ssr: false,
  loading: () => (
    <div className="flex h-full w-full items-center justify-center bg-card">
      <span className="font-mono text-sm text-muted-foreground">지도 로딩중...</span>
    </div>
  ),
})

type FilterType = "all" | "급속" | "완속"
type ListMode = "all" | "favorites"

interface Props {
  onFilteredChange?: (count: number) => void
}

export function StationExplorer({ onFilteredChange }: Props) {
  const { favorites, user, reloadFavorites } = useAuth()
  const [selected, setSelected] = useState<ChargingStation | null>(null)
  const [stations, setStations] = useState<ChargingStation[]>([])
  const [query, setQuery] = useState("")
  const [filter, setFilter] = useState<FilterType>("all")
  const [listMode, setListMode] = useState<ListMode>("all")
  const [mobileView, setMobileView] = useState<"map" | "list">("map")

  useEffect(() => {
    let mounted = true
    console.log("station-explorer: starting fetchStations()")
    fetchStations()
      .then((data) => {
        if (mounted) {
          setStations(data)
          console.log("station-explorer: fetched stations count", data.length)
        }
      })
      .catch((err) => console.error("station-explorer: fetchStations error", err))
    return () => {
      mounted = false
    }
  }, [])

  // 즐겨찾기 모드로 전환할 때 DB에서 최신 즐겨찾기 목록 로드
  useEffect(() => {
    if (listMode === "favorites" && user) {
      reloadFavorites()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [listMode, user])

  const filtered = useMemo(() => {
    return stations.filter((s) => {
      if (filter !== "all" && s.chargerType !== filter) return false
      if (listMode === "favorites" && !favorites.includes(s.id)) return false
      if (query) {
        const q = query.toLowerCase()
        return s.name.toLowerCase().includes(q) || s.address.toLowerCase().includes(q)
      }
      return true
    })
  }, [filter, listMode, query, favorites, stations])

    useEffect(() => {
    onFilteredChange?.(filtered.length)
  }, [filtered.length, onFilteredChange])

  useEffect(() => {
  

    console.log(
      "station-explorer: filtered count",
      filtered.length,
      { filter, listMode, query },
    )
  }, [filtered, filter, listMode, query])

  return (
    <div className="mx-auto grid max-w-7xl grid-cols-1 gap-4 px-4 py-6 lg:grid-cols-[420px_1fr]">
      {/* List panel */}
      <div
        className={cn(
          "flex flex-col gap-4",
          mobileView === "list" ? "flex" : "hidden lg:flex",
        )}
      >
        <div className="flex flex-col gap-3">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="충전소 이름 또는 주소 검색"
              className="w-full rounded-md border border-border bg-input py-2 pl-9 pr-3 text-sm outline-none transition-shadow focus:ring-2 focus:ring-ring"
            />
          </div>

          <div className="flex items-center gap-2">
            <div className="flex rounded-md border border-border p-0.5">
              {(["all", "급속", "완속"] as FilterType[]).map((f) => (
                <button
                  key={f}
                  onClick={() => setFilter(f)}
                  className={cn(
                    "rounded px-3 py-1 text-xs font-medium transition-colors",
                    filter === f
                      ? "bg-primary text-primary-foreground"
                      : "text-muted-foreground hover:text-foreground",
                  )}
                >
                  {f === "all" ? "전체" : f}
                </button>
              ))}
            </div>
            {user && (
              <button
                onClick={() => setListMode((m) => (m === "all" ? "favorites" : "all"))}
                className={cn(
                  "flex items-center gap-1 rounded-md border px-3 py-1.5 text-xs font-medium transition-colors",
                  listMode === "favorites"
                    ? "border-primary bg-primary/15 text-primary"
                    : "border-border text-muted-foreground hover:text-foreground",
                )}
              >
                <Star className={cn("h-3.5 w-3.5", listMode === "favorites" && "fill-primary")} />
                즐겨찾기
              </button>
            )}
          </div>

          <p className="font-mono text-xs text-muted-foreground">
            {filtered.length}개 충전소
            {listMode === "favorites" && " (즐겨찾기)"}
          </p>
        </div>

        <div className="flex flex-col gap-2 lg:max-h-[calc(100vh-16rem)] lg:overflow-y-auto lg:pr-1">
          {filtered.length === 0 ? (
            <div className="rounded-lg border border-dashed border-border p-8 text-center text-sm text-muted-foreground">
              {listMode === "favorites"
                ? "즐겨찾기한 충전소가 없습니다."
                : "검색 결과가 없습니다."}
            </div>
          ) : (
            filtered.map((s) => (
              <StationCard
                key={s.id}
                station={s}
                active={selected?.id === s.id}
                onSelect={(st) => {
                  setSelected(st)
                  setMobileView("map")
                }}
              />
            ))
          )}
        </div>
      </div>

      {/* Map panel */}
      <div
        className={cn(
          "relative h-[calc(100vh-10rem)] overflow-hidden rounded-xl border border-border",
          mobileView === "map" ? "block" : "hidden lg:block",
        )}
      >
        <MapView stations={filtered} selected={selected} onSelect={setSelected} />
      </div>

      {/* Mobile toggle */}
      <div className="fixed bottom-5 left-1/2 z-[800] flex -translate-x-1/2 rounded-full border border-border bg-card p-1 cyber-glow lg:hidden">
        <button
          onClick={() => setMobileView("map")}
          className={cn(
            "flex items-center gap-1.5 rounded-full px-4 py-2 text-sm font-medium transition-colors",
            mobileView === "map" ? "bg-primary text-primary-foreground" : "text-muted-foreground",
          )}
        >
          <MapIcon className="h-4 w-4" />
          지도
        </button>
        <button
          onClick={() => setMobileView("list")}
          className={cn(
            "flex items-center gap-1.5 rounded-full px-4 py-2 text-sm font-medium transition-colors",
            mobileView === "list" ? "bg-primary text-primary-foreground" : "text-muted-foreground",
          )}
        >
          <List className="h-4 w-4" />
          목록
        </button>
      </div>
    </div>
  )
}
