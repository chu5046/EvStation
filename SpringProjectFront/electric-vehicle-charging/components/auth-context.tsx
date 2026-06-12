"use client"

import { createContext, useContext, useEffect, useState, useCallback, type ReactNode } from "react"
import { login as loginApi, type LoginResponse } from "./api/authApi"
import { getFavorites, addFavorite, removeFavorite } from "./api/favoritesApi"

interface User {
  memberId: string
  name: string
  email: string
}

interface AuthContextValue {
  user: User | null
  accessToken: string | null
  favorites: string[]
  authModalOpen: boolean
  authMode: "login" | "signup"
  openAuth: (mode: "login" | "signup") => void
  closeAuth: () => void
  login: (email: string, password: string) => Promise<void>
  logout: () => void
  toggleFavorite: (stationId: string) => void
  isFavorite: (stationId: string) => boolean
  reloadFavorites: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

const TOKEN_KEY = "volt_access_token"
const USER_KEY = "volt_user"
// favorites are stored in backend; no local FAVORITES_KEY anymore

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [accessToken, setAccessToken] = useState<string | null>(null)
  const [favorites, setFavorites] = useState<string[]>([])
  const [authModalOpen, setAuthModalOpen] = useState(false)
  const [authMode, setAuthMode] = useState<"login" | "signup">("login")

  // 새로고침 시 localStorage에서 인증 상태 복원
  useEffect(() => {
    try {
      const token = localStorage.getItem(TOKEN_KEY)
      const u = localStorage.getItem(USER_KEY)
      if (token) setAccessToken(token)
      if (u) setUser(JSON.parse(u))
      // if there's a user stored, load favorites from backend
      if (u) {
        try {
          const parsed = JSON.parse(u)
          if (parsed?.memberId) {
            getFavorites(parsed.memberId)
              .then((ids) => setFavorites(ids || []))
              .catch(() => {
                // ignore failures and keep empty favorites
              })
          }
        } catch {
          // ignore
        }
      }
    } catch {
      // ignore
    }
  }, [])

  const openAuth = useCallback((mode: "login" | "signup") => {
    setAuthMode(mode)
    setAuthModalOpen(true)
  }, [])

  const closeAuth = useCallback(() => setAuthModalOpen(false), [])

  // 실제 로그인 API 호출 후 상태 + localStorage 저장
  const login = useCallback(async (email: string, password: string) => {
    const data: LoginResponse = await loginApi({ email, password })
    const nextUser: User = {
      memberId: data.memberId,
      name: data.name,
      email: data.email,
    }
    setAccessToken(data.accessToken)
    setUser(nextUser)
    localStorage.setItem(TOKEN_KEY, data.accessToken)
    localStorage.setItem(USER_KEY, JSON.stringify(nextUser))
    // load favorites for the logged in user
    try {
      const ids = await getFavorites(nextUser.memberId)

      console.log("GET FAVORITES RESULT")
      console.log(JSON.stringify(ids, null, 2))

      setFavorites(ids || [])
    } catch {
      setFavorites([])
    }

    setAuthModalOpen(false)
  }, [])

  const logout = useCallback(() => {
    setUser(null)
    setAccessToken(null)
    setFavorites([])
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    window.location.reload()
    alert("로그아웃되었습니다.")
  }, [])

  const toggleFavorite = useCallback(
    async (stationId: string) => {
      if (!user) return
      const isFav = favorites.includes(stationId)
      if (isFav) {
        try {
          await removeFavorite(user.memberId, stationId)
          setFavorites((prev) => prev.filter((id) => id !== stationId))
        } catch (err) {
          console.error("failed to remove favorite", err)
        }
      } else {
        try {
          await addFavorite({ userId: user.memberId, stationId })
          setFavorites((prev) => [...prev, stationId])
        } catch (err) {
          console.error("failed to add favorite", err)
        }
      }
    },
    [user, favorites],
  )

  const isFavorite = useCallback((stationId: string) => favorites.includes(stationId), [favorites])

  const reloadFavorites = useCallback(async () => {
    if (!user) {
      console.log("reloadFavorites: no user")
      return
    }
    try {
      console.log("reloadFavorites: calling getFavorites with userId:", user.memberId)
      const ids = await getFavorites(user.memberId)
      console.log("reloadFavorites: got favorites:", ids)
      setFavorites(ids || [])
    } catch (err) {
      console.error("reloadFavorites: failed", err)
    }
  }, [user])

  return (
    <AuthContext.Provider
      value={{
        user,
        accessToken,
        favorites,
        authModalOpen,
        authMode,
        openAuth,
        closeAuth,
        login,
        logout,
        toggleFavorite,
        isFavorite,
        reloadFavorites,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error("useAuth must be used within AuthProvider")
  return ctx
}
