"use client"

import { useAuth } from "./auth-context"
import { Button } from "@/components/ui/button"
import { Zap, LogOut, User } from "lucide-react"

import { useState } from "react"
import { Settings } from "lucide-react"
import { MemberModal } from "./member-modal"

export function SiteHeader() {
  const { user, openAuth, logout } = useAuth()
  const [memberOpen, setMemberOpen] =useState(false)

  return (
     <>
    <header className="sticky top-0 z-[900] border-b border-border bg-background/80 backdrop-blur-md">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4">
        <div className="flex items-center gap-2">
          <div className="flex h-9 w-9 items-center justify-center rounded-md bg-primary/15 cyber-glow">
            <Zap className="h-5 w-5 text-primary text-glow" />
          </div>
          <div className="flex flex-col leading-none">
            <span className="font-mono text-lg font-bold tracking-tight text-foreground">
              VOLT
            </span>
            <span className="text-[10px] uppercase tracking-[0.2em] text-muted-foreground">
              EV Charge Station Grid
            </span>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {user ? (
            <>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setMemberOpen(true)}
              >
                <Settings className="h-4 w-4" />
                회원정보
              </Button>
              <div className="hidden items-center gap-2 rounded-md border border-border bg-card px-3 py-1.5 sm:flex">
                <User className="h-4 w-4 text-primary" />
                <span className="text-sm font-medium">{user.name}</span>
              </div>
              <Button variant="outline" size="sm" onClick={logout} className="gap-1.5">
                <LogOut className="h-4 w-4" />
                로그아웃
              </Button>
            </>
          ) : (
            <>
              <Button variant="ghost" size="sm" onClick={() => openAuth("login")}>
                로그인
              </Button>
              <Button size="sm" onClick={() => openAuth("signup")} className="font-semibold">
                회원가입
              </Button>
            </>
          )}
        </div>
      </div>
              <MemberModal
          open={memberOpen}
          onClose={() =>
            setMemberOpen(false)
          }
        />
    </header>
<MemberModal
      open={memberOpen}
      onClose={() => setMemberOpen(false)}
    />
  </>
          
  )
}
