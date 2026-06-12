"use client"

import { useEffect, useMemo } from "react"
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet"
import L from "leaflet"
import { Star } from "lucide-react"
import type { ChargingStation } from "@/lib/stations"
import { DAEGU_CENTER } from "@/lib/stations"
import { useAuth } from "./auth-context"
import { cn } from "@/lib/utils"

const STATUS_COLOR: Record<ChargingStation["status"], string> = {
  available: "#3ddc84",
  charging: "#39d3e6",
  unavailable: "#e6555a",
}

function makeIcon(station: ChargingStation, active: boolean) {
  const color = STATUS_COLOR[station.status]
  return L.divIcon({
    className: "",
    html: `
      <div style="position:relative;width:34px;height:34px;">
        <div style="
          position:absolute;inset:0;border-radius:50%;
          background:${color};opacity:0.25;
          ${active ? "animation:cyberPulse 1.4s ease-out infinite;" : ""}
        "></div>
        <div style="
          position:absolute;inset:8px;border-radius:50%;
          background:${color};
          box-shadow:0 0 12px ${color};
          border:2px solid rgba(255,255,255,0.85);
          display:flex;align-items:center;justify-content:center;
          font-size:12px;color:#0b1220;font-weight:700;
        ">⚡</div>
      </div>`,
    iconSize: [34, 34],
    iconAnchor: [17, 17],
    popupAnchor: [0, -16],
  })
}

function FlyTo({ station }: { station: ChargingStation | null }) {
  const map = useMap()
  useEffect(() => {
    if (station) {
      map.flyTo([station.lat, station.lng], 15, { duration: 0.8 })
    }
  }, [station, map])
  return null
}

interface MapViewProps {
  stations: ChargingStation[]
  selected: ChargingStation | null
  onSelect: (station: ChargingStation) => void
}

export default function MapView({ stations, selected, onSelect }: MapViewProps) {
  const { isFavorite, toggleFavorite, user, openAuth } = useAuth()
  useEffect(() => {
    console.log("MapView: stations count", stations.length, "selectedId", selected?.id ?? null)
  }, [stations, selected])
  const icons = useMemo(
    () => new Map(stations.map((s) => [s.id, makeIcon(s, selected?.id === s.id)])),
    [stations, selected],
  )

  return (
    <MapContainer
      center={[DAEGU_CENTER.lat, DAEGU_CENTER.lng]}
      zoom={12}
      scrollWheelZoom
      style={{ height: "100%", width: "100%" }}
    >
      <TileLayer
        attribution='&copy; OpenStreetMap'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <FlyTo station={selected} />
      {stations.map((s) => {
        const fav = isFavorite(s.id)
        return (
          <Marker
            key={s.id}
            position={[s.lat, s.lng]}
            icon={icons.get(s.id)}
            eventHandlers={{ click: () => onSelect(s) }}
          >
            <Popup>
              <div style={{ minWidth: 200 }}>
                <strong style={{ color: "#39d3e6" }}>{s.name}</strong>
                <div style={{ fontSize: 12, marginTop: 4, opacity: 0.85 }}>{s.address}</div>
                <div style={{ fontSize: 12, marginTop: 6 }}>
                  {s.chargerType} · {s.power} · 사용가능 {s.availableChargers}/{s.totalChargers}
                </div>
                <button
                  onClick={() => {
                    if (!user) {
                      openAuth("login")
                      return
                    }
                    toggleFavorite(s.id)
                  }}
                  className={cn(
                    "mt-2.5 flex w-full items-center justify-center gap-1.5 rounded-md border px-3 py-1.5 text-xs font-semibold transition-colors",
                    fav
                      ? "border-[#39d3e6] bg-[#39d3e6]/15 text-[#39d3e6]"
                      : "border-[#39d3e6]/40 text-[#39d3e6] hover:bg-[#39d3e6]/10",
                  )}
                >
                  <Star size={14} className={cn(fav && "fill-[#39d3e6]")} />
                  {fav ? "즐겨찾기 해제" : "즐겨찾기 추가"}
                </button>
              </div>
            </Popup>
          </Marker>
        )
      })}
    </MapContainer>
  )
}
