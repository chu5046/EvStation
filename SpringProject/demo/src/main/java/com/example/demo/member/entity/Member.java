package com.example.demo.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;
}
