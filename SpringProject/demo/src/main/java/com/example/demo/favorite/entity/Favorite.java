package com.example.demo.favorite.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "favorite")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id
    private String id;

    private String userId;

    private String stationId;

    private LocalDateTime createdAt;

    // userId와 stationId로 생성하는 생성자 (DB 저장 시 사용)
    public Favorite(String userId, String stationId) {
        this.userId = userId;
        this.stationId = stationId;
        this.createdAt = LocalDateTime.now();
    }
}
