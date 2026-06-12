package com.example.demo.auth.controller;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 인증 관련 API Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 회원 서비스 의존성
    private final MemberService memberService;

    // 생성자 주입
    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    /*
     * 회원 로그인
     *
     * POST /api/auth/login
     * 
     * 요청 본문:
     * {
     * "email": "user@example.com",
     * "password": "password123"
     * }
     * 
     * 응답:
     * {
     * "accessToken": "eyJhbGc...",
     * "memberId": "12345",
     * "name": "John Doe",
     * "email": "user@example.com"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        System.out.println("===== LOGIN API 진입 =====");
        // 로그인 처리
        LoginResponseDto response = memberService.login(requestDto);

        // HTTP 200 OK 와 함께 JWT 토큰과 회원 정보 반환
        return ResponseEntity.ok(response);
    }
}
