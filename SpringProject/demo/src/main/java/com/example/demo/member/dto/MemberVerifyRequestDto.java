package com.example.demo.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberVerifyRequestDto {

    private String userId;
    private String password;
}
