package com.example.demo.member.service;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.member.dto.MemberRequestDto;
import com.example.demo.member.dto.MemberResponseDto;

import java.util.List;

public interface MemberService {

    MemberResponseDto createMember(MemberRequestDto requestDto);

    List<MemberResponseDto> getAllMembers();

    MemberResponseDto getMemberById(String id);

    MemberResponseDto updateMember(String id, MemberRequestDto requestDto);

    void deleteMember(String id);

    LoginResponseDto login(LoginRequestDto requestDto);

    boolean verifyMember(String userId, String password);

    // 회원정보 수정 - name
    void updateMemberName(String userId, String name);

    // 회원정보 수정 - email
    void updateMemberEmail(String userId, String email);

    // 회원정보 수정 - password
    void updateMemberPassword(String userId, String password);
}
