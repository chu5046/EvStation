package com.example.demo.member.service;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.auth.util.JwtUtil;
import com.example.demo.member.dto.MemberRequestDto;
import com.example.demo.member.dto.MemberResponseDto;
import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// Service 계층 Bean 등록
@Service
public class MemberServiceImpl implements MemberService {

    // 회원 데이터 접근을 위한 Repository
    private final MemberRepository memberRepository;

    // JWT 토큰 생성 및 검증
    private final JwtUtil jwtUtil;

    // 비밀번호 암호화
    private final BCryptPasswordEncoder passwordEncoder;

    // 생성자 주입
    public MemberServiceImpl(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 회원 생성
    @Override
    public MemberResponseDto createMember(MemberRequestDto requestDto) {
        System.out.println("===== CREATE MEMBER SERVICE  진입 =====");

        // 이메일 중복 체크
        if (memberRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // Member 객체 생성
        Member member = new Member();

        // 요청 DTO의 값을 사용하여 회원 객체 생성
        // 비밀번호는 암호화하여 저장
        member = new Member(
                null,
                requestDto.getName(),
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword()));

        // DB 저장
        Member saved = memberRepository.save(member);
        System.out.println("회원 저장 완료: " + saved.getId());

        // Entity -> Response DTO 변환 후 반환
        return toResponseDto(saved);
    }

    // 전체 회원 조회
    @Override
    public List<MemberResponseDto> getAllMembers() {

        // 모든 회원 조회
        return memberRepository.findAll().stream()

                // 각 Member를 MemberResponseDto로 변환
                .map(this::toResponseDto)

                // List로 수집
                .collect(Collectors.toList());
    }

    // 회원 단건 조회
    @Override
    public MemberResponseDto getMemberById(String id) {

        // id에 해당하는 회원 조회
        // 없으면 예외 발생
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with id: " + id));

        // Entity -> DTO 변환
        return toResponseDto(member);
    }

    // 회원 정보 수정
    @Override
    public MemberResponseDto updateMember(String id, MemberRequestDto requestDto) {

        // 수정할 회원 조회
        // 없으면 예외 발생
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with id: " + id));

        // 기존 id를 유지한 채 새로운 정보로 객체 생성
        member = new Member(
                member.getId(),
                requestDto.getName(),
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword()));

        // 수정 내용 저장
        Member updated = memberRepository.save(member);

        // Entity -> DTO 변환
        return toResponseDto(updated);
    }

    // 회원 삭제
    @Override
    public void deleteMember(String id) {

        // 회원 존재 여부 확인
        if (!memberRepository.existsById(id)) {

            // 존재하지 않으면 예외 발생
            throw new IllegalArgumentException(
                    "Member not found with id: " + id);
        }

        // 회원 삭제
        memberRepository.deleteById(id);
    }

    // 로그인
    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        System.out.println("입력 이메일: " + requestDto.getEmail());

        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with email: " + requestDto.getEmail()));

        System.out.println("입력 비밀번호: " + requestDto.getPassword());
        System.out.println("암호화 비밀번호: " + member.getPassword());
        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        System.out.println("비밀번호 검증 성공");

        System.out.println("JWT 생성 시작");

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateToken(member.getId(), member.getEmail(), member.getName());
        System.out.println("JWT 생성 성공");

        // 로그인 응답 DTO 반환
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }

    // userId와 password로 검증
    @Override
    public boolean verifyMember(String userId, String password) {
        System.out.println("===== VERIFY MEMBER SERVICE 진입 =====");
        System.out.println("[MemberService] 검증 userId=" + userId);
        try {
            Optional<Member> optionalMember = memberRepository.findById(userId);
            if (optionalMember.isEmpty()) {
                System.out.println("[MemberService] 멤버를 찾을 수 없음: " + userId);
                return false;
            }

            Member member = optionalMember.get();
            boolean matched = passwordEncoder.matches(password, member.getPassword());
            System.out.println("[MemberService] 비밀번호 일치 여부=" + matched);
            return matched;
        } catch (Exception e) {
            System.out.println("[MemberService] verifyMember 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("회원 검증 중 오류가 발생했습니다.", e);
        }
    }

    // 회원정보 수정 - name
    @Override
    public void updateMemberName(String userId, String name) {
        System.out.println("===== UPDATE MEMBER NAME SERVICE 진입 =====");
        System.out.println("[MemberService] userId=" + userId + ", 새 name=" + name);
        try {
            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Member not found with id: " + userId));

            Member updated = new Member(
                    member.getId(),
                    name,
                    member.getEmail(),
                    member.getPassword());

            memberRepository.save(updated);
            System.out.println("[MemberService] 이름 수정 완료: " + userId);
        } catch (Exception e) {
            System.out.println("[MemberService] updateMemberName 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이름 수정 중 오류가 발생했습니다.", e);
        }
    }

    // 회원정보 수정 - email
    @Override
    public void updateMemberEmail(String userId, String email) {
        System.out.println("===== UPDATE MEMBER EMAIL SERVICE 진입 =====");
        System.out.println("[MemberService] userId=" + userId + ", 새 email=" + email);
        try {
            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Member not found with id: " + userId));

            Member updated = new Member(
                    member.getId(),
                    member.getName(),
                    email,
                    member.getPassword());

            memberRepository.save(updated);
            System.out.println("[MemberService] 이메일 수정 완료: " + userId);
        } catch (Exception e) {
            System.out.println("[MemberService] updateMemberEmail 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이메일 수정 중 오류가 발생했습니다.", e);
        }
    }

    // 회원정보 수정 - password
    @Override
    public void updateMemberPassword(String userId, String password) {
        System.out.println("===== UPDATE MEMBER PASSWORD SERVICE 진입 =====");
        System.out.println("[MemberService] userId=" + userId);
        try {
            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Member not found with id: " + userId));

            Member updated = new Member(
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    passwordEncoder.encode(password));

            memberRepository.save(updated);
            System.out.println("[MemberService] 비밀번호 수정 완료: " + userId);
        } catch (Exception e) {
            System.out.println("[MemberService] updateMemberPassword 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("비밀번호 수정 중 오류가 발생했습니다.", e);
        }
    }

    // Entity를 Response DTO로 변환하는 내부 메서드
    private MemberResponseDto toResponseDto(Member member) {

        return new MemberResponseDto(
                member.getId(),
                member.getName(),
                member.getEmail());
    }
}