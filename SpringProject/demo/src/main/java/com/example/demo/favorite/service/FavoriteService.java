package com.example.demo.favorite.service;

import com.example.demo.favorite.dto.FavoriteRequestDto;
import com.example.demo.favorite.dto.FavoriteResponseDto;
import com.example.demo.station.Station;
import java.util.List;

public interface FavoriteService {

    // 즐겨찾기 저장
    FavoriteResponseDto saveFavorite(FavoriteRequestDto requestDto);

    // userId의 모든 즐겨찾기 조회
    List<FavoriteResponseDto> getFavoritesByUserId(String userId);

    // userId의 모든 즐겨찾기 역정보 조회
    // userId -> stationId 리스트 -> Station 정보 리스트로 변환해서 반환
    List<Station> getFavoriteStationsByUserId(String userId);

    // userId와 stationId로 즐겨찾기 확인
    boolean isFavorite(String userId, String stationId);

    // userId와 stationId로 즐겨찾기 삭제
    void deleteFavorite(String userId, String stationId);

    // userId의 즐겨찾기 개수
    long getFavoriteCount(String userId);
}
