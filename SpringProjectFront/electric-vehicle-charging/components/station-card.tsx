"use client"

import type { ChargingStation } from "@/lib/stations"
import { useAuth } from "./auth-context"
import { Star, MapPin, Zap } from "lucide-react"
import { cn } from "@/lib/utils"

const STATUS_META: Record<
  ChargingStation["status"],
  { label: string; dot: string; text: string }
> = {
  available: { label: "사용가능", dot: "bg-accent", text: "text-accent" },
  charging: { label: "충전중", dot: "bg-primary", text: "text-primary" },
  unavailable: { label: "사용불가", dot: "bg-destructive", text: "text-destructive" },
}

interface StationCardProps {
  station: ChargingStation
  active: boolean
  onSelect: (station: ChargingStation) => void
}

export function StationCard({ station, active, onSelect }: StationCardProps) {
  const { user, isFavorite, toggleFavorite, openAuth } = useAuth()
  const status = STATUS_META[station.status]
  const fav = isFavorite(station.id)

  async function handleFav(e: React.MouseEvent) {
    e.stopPropagation()
    if (!user) {
      openAuth("login")
      return
    }
    await toggleFavorite(station.id)
  }

  return (
    <button
      onClick={() => onSelect(station)}
      className={cn(
        "group flex w-full flex-col gap-2 rounded-lg border bg-card p-4 text-left transition-all hover:border-primary/60",
        active ? "border-primary cyber-glow" : "border-border",
      )}
    >
      <div className="flex items-start justify-between gap-2">
        <div className="flex items-center gap-2">
          <span
            className={cn(
              "rounded px-1.5 py-0.5 text-[10px] font-bold",
              station.chargerType === "급속"
                ? "bg-primary/15 text-primary"
                : "bg-accent/15 text-accent",
            )}
          >
            {station.chargerType}
          </span>
          <h3 className="text-sm font-semibold text-foreground">{station.name}</h3>
        </div>
        {user && (
          <span
            onClick={handleFav}
            role="button"
            tabIndex={0}
            aria-label={fav ? "즐겨찾기 해제" : "즐겨찾기 추가"}
            className="shrink-0 rounded p-0.5 text-muted-foreground transition-colors hover:text-primary"
          >
            <Star className={cn("h-4 w-4", fav && "fill-primary text-primary")} />
          </span>
        )}
      </div>

      <div className="flex items-center gap-1 text-xs text-muted-foreground">
        <MapPin className="h-3.5 w-3.5 shrink-0" />
        <span className="truncate">{station.address}</span>
      </div>

      <div className="flex items-center justify-between pt-1">
        <div className="flex items-center gap-3 text-xs text-muted-foreground">
          <span className="flex items-center gap-1">
            <Zap className="h-3.5 w-3.5 text-primary" />
            {station.power}
          </span>
          <span>{station.pricePerKwh}원/kWh</span>
        </div>
        <div className="flex items-center gap-1.5">
          <span className={cn("h-2 w-2 rounded-full", status.dot)} />
          <span className={cn("text-xs font-medium", status.text)}>
            {status.label} {station.availableChargers}/{station.totalChargers}
          </span>
        </div>
      </div>
    </button>
  )
}
