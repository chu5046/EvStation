"use client"

import { AuthProvider } from "@/components/auth-context"
import { SiteHeader } from "@/components/site-header"
import { AuthModal } from "@/components/auth-modal"
import { StationExplorer } from "@/components/station-explorer"
import { Zap, Radio, MapPin } from "lucide-react"
import { useState } from "react"


export default function Home() {
   const [filteredCount, setFilteredCount] = useState(0)
  console.log("PAGE RENDERED")
  return (
    <AuthProvider>
      <div className="min-h-screen cyber-grid">
        <SiteHeader />

        <section className="border-b border-border">
          <div className="mx-auto max-w-7xl px-4 py-8">
            <div className="flex flex-wrap items-end justify-between gap-4">
              <div className="max-w-2xl">
                <div className="mb-3 inline-flex items-center gap-1.5 rounded-full border border-primary/40 bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
                  <Radio className="h-3 w-3" />
                  실시간 충전소 그리드 · 대구광역시
                  
                </div>
                <h1 className="text-balance font-mono text-3xl font-bold tracking-tight text-foreground text-glow sm:text-4xl">
                  전기차 충전소를 한눈에
                </h1>
                <p className="mt-2 text-pretty text-sm leading-relaxed text-muted-foreground sm:text-base">
                  지도와 목록으로 가까운 충전소를 찾고, 즐겨찾기로 자주 가는 곳을 저장하세요.
                   </p>
                   <p>충전소 데이터는 공공 데이터 오픈 API로 실시간 연동됩니다.</p>
               
              </div>

              <div className="flex gap-3">
                <Stat icon={<Zap className="h-4 w-4" />} value={filteredCount.toString()} label="충전소" />
                <Stat icon={<MapPin className="h-4 w-4" />} value="대구" label="서비스 지역" />
              </div>
            </div>
          </div>
        </section>

        <StationExplorer  onFilteredChange={setFilteredCount} />
        <AuthModal />
      </div>
    </AuthProvider>
  )
}

function Stat({ icon, value, label }: { icon: React.ReactNode; value: string; label: string }) {
  return (
    <div className="flex flex-col items-center justify-center rounded-lg border border-border bg-card px-5 py-3">
      <span className="flex items-center gap-1 text-primary">{icon}</span>
      <span className="mt-1 font-mono text-xl font-bold text-foreground">{value}</span>
      <span className="text-[10px] uppercase tracking-wider text-muted-foreground">{label}</span>
    </div>
  )
}
