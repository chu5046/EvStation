package com.example.demo.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponseDto {

    private String id;

    private String userId;

    private String stationId;

    private LocalDateTime createdAt;
}
