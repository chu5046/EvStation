"use client"

import { useState } from "react"
import { useAuth } from "./auth-context"
import { signup } from "./api/authApi"
import { Button } from "@/components/ui/button"
import { X, Zap } from "lucide-react"

export function AuthModal() {
  const { authModalOpen, authMode, closeAuth, login, openAuth } = useAuth()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [name, setName] = useState("")
  const [error, setError] = useState("")
  const [info, setInfo] = useState("")
  const [loading, setLoading] = useState(false)

  if (!authModalOpen) return null

  const isSignup = authMode === "signup"

  // 비밀번호 일치 여부 (입력 중 실시간 표시)
  const passwordMismatch = isSignup && confirmPassword.length > 0 && password !== confirmPassword

  function resetFields() {
    setEmail("")
    setPassword("")
    setConfirmPassword("")
    setName("")
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError("")
    setInfo("")

    if (isSignup && !name.trim()) {
      setError("이름을 입력해 주세요.")
      return
    }
    if (!email.trim()) {
      setError("아이디를 입력해 주세요.")
      return
    }
    if (!password.trim()) {
      setError("비밀번호를 입력해 주세요.")
      return
    }
    if (isSignup && password.length < 6) {
      setError("비밀번호는 6자리 이상이어야 합니다.")
      return
    }
    if (isSignup && password !== confirmPassword) {
      setError("비밀번호가 일치하지 않습니다.")
      return
    }

    setLoading(true)
    try {
      if (isSignup) {
        await signup({ name, email, password })
        resetFields()
        setInfo("회원가입이 완료되었습니다. 로그인해 주세요.")
        openAuth("login")
      } else {
        await login(email, password)
        resetFields()
      }
    } catch (err: unknown) {
      const message =
        (typeof err === "object" &&
          err !== null &&
          "response" in err &&
          (err as { response?: { data?: { message?: string } } }).response?.data?.message) ||
        (isSignup ? "회원가입에 실패했습니다. 다시 시도해 주세요." : "로그인에 실패했습니다. 아이디와 비밀번호를 확인해 주세요.")
      setError(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      className="fixed inset-0 z-[1000] flex items-center justify-center bg-background/80 p-4 backdrop-blur-sm"
      onClick={closeAuth}
      role="dialog"
      aria-modal="true"
      aria-label={isSignup ? "회원가입" : "로그인"}
    >
      <div
        className="cyber-glow relative w-full max-w-sm rounded-xl border border-border bg-card p-6"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          onClick={closeAuth}
          className="absolute right-4 top-4 text-muted-foreground transition-colors hover:text-foreground"
          aria-label="닫기"
        >
          <X className="h-5 w-5" />
        </button>

        <div className="mb-6 flex items-center gap-2">
          <Zap className="h-6 w-6 text-primary text-glow" />
          <h2 className="font-mono text-xl font-bold tracking-tight">
            {isSignup ? "회원가입" : "로그인"}
          </h2>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          {isSignup && (
            <div className="flex flex-col gap-1.5">
              <label htmlFor="name" className="text-xs font-medium text-muted-foreground">
                이름
              </label>
              <input
                id="name"
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="rounded-md border border-border bg-input px-3 py-2 text-sm outline-none transition-shadow focus:ring-2 focus:ring-ring"
                placeholder="홍길동"
              />
            </div>
          )}
          <div className="flex flex-col gap-1.5">
            <label htmlFor="email" className="text-xs font-medium text-muted-foreground">
              아이디
            </label>
            <input
              id="email"
              type="text"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="rounded-md border border-border bg-input px-3 py-2 text-sm outline-none transition-shadow focus:ring-2 focus:ring-ring"
              placeholder="아이디를 입력하세요"
            />
          </div>
          <div className="flex flex-col gap-1.5">
            <label htmlFor="password" className="text-xs font-medium text-muted-foreground">
              비밀번호{isSignup && <span className="ml-1 text-muted-foreground">(6자리 이상)</span>}
            </label>
            <input
              id="password"
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="rounded-md border border-border bg-input px-3 py-2 text-sm outline-none transition-shadow focus:ring-2 focus:ring-ring"
              placeholder="••••••••"
            />
          </div>

          {/* 회원가입 시에만 비밀번호 확인 필드 노출 */}
          {isSignup && (
            <div className="flex flex-col gap-1.5">
              <label htmlFor="confirmPassword" className="text-xs font-medium text-muted-foreground">
                비밀번호 확인
              </label>
              <input
                id="confirmPassword"
                type="password"
                required
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className={`rounded-md border bg-input px-3 py-2 text-sm outline-none transition-shadow focus:ring-2 focus:ring-ring ${
                  passwordMismatch ? "border-destructive focus:ring-destructive/50" : "border-border"
                }`}
                placeholder="••••••••"
              />
              {/* 실시간 불일치 안내 */}
              {passwordMismatch && (
                <p className="text-xs text-destructive">비밀번호가 일치하지 않습니다.</p>
              )}
              {/* 일치 확인 안내 */}
              {isSignup && confirmPassword.length > 0 && !passwordMismatch && (
                <p className="text-xs text-primary">비밀번호가 일치합니다.</p>
              )}
            </div>
          )}

          {info && (
            <p className="rounded-md border border-primary/40 bg-primary/10 px-3 py-2 text-xs text-primary" role="status">
              {info}
            </p>
          )}

          {error && (
            <p className="rounded-md border border-destructive/40 bg-destructive/10 px-3 py-2 text-xs text-destructive" role="alert">
              {error}
            </p>
          )}

          <Button type="submit" disabled={loading} className="mt-2 w-full font-semibold">
            {loading ? "처리 중..." : isSignup ? "가입하기" : "로그인"}
          </Button>
        </form>

        <div className="mt-4 text-center text-xs text-muted-foreground">
          {isSignup ? "이미 계정이 있으신가요?" : "아직 계정이 없으신가요?"}{" "}
          <button
            onClick={() => openAuth(isSignup ? "login" : "signup")}
            className="font-medium text-primary hover:underline"
          >
            {isSignup ? "로그인" : "회원가입"}
          </button>
        </div>
      </div>
    </div>
  )
}
