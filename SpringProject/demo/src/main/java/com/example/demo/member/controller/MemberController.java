package com.example.demo.member.controller;

// 요청 DTO (회원 생성/수정 시 사용)
import com.example.demo.member.dto.MemberRequestDto;
import com.example.demo.member.dto.MemberVerifyRequestDto;
import com.example.demo.member.dto.MemberUpdateNameRequestDto;
import com.example.demo.member.dto.MemberUpdateEmailRequestDto;
import com.example.demo.member.dto.MemberUpdatePasswordRequestDto;

// 응답 DTO (회원 조회 결과 반환 시 사용)
import com.example.demo.member.dto.MemberResponseDto;

// 비즈니스 로직 처리 서비스
import com.example.demo.member.service.MemberService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

// REST API Controller임을 나타냄
@RestController

// 공통 URL 경로
// 모든 API는 /api/members 로 시작
@RequestMapping("/api/members")
public class MemberController {

    // Service 계층 의존성
    private final MemberService memberService;

    // 생성자 주입
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /*
     * 회원 생성
     *
     * POST /api/members
     */
    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(
            @RequestBody MemberRequestDto requestDto) {

        // Service에게 회원 생성 요청
        System.out.println("===== CREATE MEMBER API 진입 =====");
        MemberResponseDto response = memberService.createMember(requestDto);

        // HTTP 200 OK 와 함께 결과 반환
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", e.getMessage()));
    }

    /*
     * userId / password 검증
     *
     * POST /api/members/verify
     *
     * 요청 본문 예시:
     * {
     * "userId": "user123",
     * "password": "password"
     * }
     */
    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyMember(
            @RequestBody MemberVerifyRequestDto requestDto) {

        System.out.println("===== VERIFY MEMBER API 진입 =====");
        System.out.println("[MemberController] 요청 userId=" + requestDto.getUserId());
        boolean verified = memberService.verifyMember(requestDto.getUserId(), requestDto.getPassword());
        System.out.println("[MemberController] verifyMember 결과=" + verified);
        return ResponseEntity.ok(verified);
    }

    /*
     * 전체 회원 조회
     *
     * GET /api/members
     */
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {

        // 전체 회원 목록 조회
        return ResponseEntity.ok(
                memberService.getAllMembers());
    }

    /*
     * 특정 회원 조회
     *
     * GET /api/members/{id}
     *
     * 예:
     * GET /api/members/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getMemberById(
            @PathVariable String id) {

        // URL 경로의 id 값을 받아 회원 조회
        return ResponseEntity.ok(
                memberService.getMemberById(id));
    }

    /*
     * 회원 정보 수정
     *
     * PUT /api/members/name
     *
     * 예:
     * PUT /api/members/1
     */
    @PutMapping("/name")
    public ResponseEntity<MemberResponseDto> updateMemberByName(
            @RequestBody MemberUpdateNameRequestDto requestDto) {
        memberService.updateMemberName(requestDto.getUserId(), requestDto.getName());
        // 회원 수정 후 결과 반환
        return ResponseEntity.ok().build();
    }

    @PutMapping("/email")
    public ResponseEntity<MemberResponseDto> updateMemberByEmail(
            @RequestBody MemberUpdateEmailRequestDto requestDto) {

        memberService.updateMemberEmail(requestDto.getUserId(), requestDto.getEmail());
        return ResponseEntity.ok().build();
    }

    // 회원 정보 수정 - password
    @PutMapping("/password")
    public ResponseEntity<MemberResponseDto> updateMemberByPassword(
            @RequestBody MemberUpdatePasswordRequestDto requestDto) {
        memberService.updateMemberPassword(requestDto.getUserId(), requestDto.getPassword());
        // 회원 수정 후 결과 반환
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponseDto> updateMemberByPassword(
            @PathVariable String id,
            @RequestBody MemberRequestDto requestDto) {

        // 회원 수정 후 결과 반환
        return ResponseEntity.ok(
                memberService.updateMember(id, requestDto));
    }

    /*
     * 회원 삭제
     *
     * DELETE /api/members/{id}
     *
     * 예:
     * DELETE /api/members/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable String id) {

        // 회원 삭제
        memberService.deleteMember(id);

        // HTTP 204 No Content 반환
        return ResponseEntity.noContent().build();
    }
}