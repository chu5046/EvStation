// authApi.ts
// 회원가입 / 로그인 백엔드 API 호출 모듈

import { api } from "./axios"

export interface SignupRequest {
  name: string
  email: string
  password: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  memberId: string
  name: string
  email: string
}

/**
 * 회원가입
 * POST /api/members
 */
export async function signup({ name, email, password }: SignupRequest): Promise<void> {
  const response = await api.post("/api/members", { name, email, password })
  return response.data
}

/**
 * 로그인
 * POST /api/auth/login
 */
export async function login({ email, password }: LoginRequest): Promise<LoginResponse> {
  const response = await api.post<LoginResponse>("/api/auth/login", { email, password })
  return response.data
}
