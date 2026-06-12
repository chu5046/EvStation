package com.example.demo.favorite.service;

import com.example.demo.favorite.dto.FavoriteRequestDto;
import com.example.demo.favorite.dto.FavoriteResponseDto;
import com.example.demo.favorite.entity.Favorite;
import com.example.demo.favorite.repository.FavoriteRepository;
import com.example.demo.station.Station;
import com.example.demo.station.service.StationService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StationService stationService;

    // 생성자 주입
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, StationService stationService) {
        this.favoriteRepository = favoriteRepository;
        this.stationService = stationService;
    }

    // 즐겨찾기 저장
    @Override
    public FavoriteResponseDto saveFavorite(FavoriteRequestDto requestDto) {
        System.out.println("===== SAVE FAVORITE SERVICE 진입 =====");
        System.out.println(
                "[FavoriteService] 요청 userId=" + requestDto.getUserId() + ", stationId=" + requestDto.getStationId());
        try {
            // userId와 stationId로 이미 저장되었는지 확인
            if (favoriteRepository.findByUserIdAndStationId(
                    requestDto.getUserId(), requestDto.getStationId()).isPresent()) {
                throw new IllegalArgumentException(
                        "이미 즐겨찾기로 저장된 역입니다.");
            }

            // 새로운 Favorite 객체 생성
            Favorite favorite = new Favorite(
                    requestDto.getUserId(),
                    requestDto.getStationId());

            // DB에 저장
            Favorite saved = favoriteRepository.save(favorite);
            System.out.println("[FavoriteService] 즐겨찾기 저장 완료: " + saved.getId());

            // Entity -> Response DTO 변환 후 반환
            return toResponseDto(saved);
        } catch (Exception e) {
            System.out.println("[FavoriteService] saveFavorite 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("즐겨찾기 저장 중 오류가 발생했습니다.", e);
        }
    }

    // userId의 모든 즐겨찾기 조회
    @Override
    public List<FavoriteResponseDto> getFavoritesByUserId(String userId) {
        System.out.println("===== GET FAVORITES SERVICE 진입 =====");
        System.out.println("[FavoriteService] 요청 userId=" + userId);
        try {
            List<FavoriteResponseDto> results = favoriteRepository.findByUserId(userId).stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
            System.out.println("[FavoriteService] getFavoritesByUserId 조회 완료, 개수=" + results.size());
            return results;
        } catch (Exception e) {
            System.out.println("[FavoriteService] getFavoritesByUserId 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("즐겨찾기 조회 중 오류가 발생했습니다.", e);
        }
    }

    // userId의 모든 즐겨찾기 역정보 조회
    // userId -> stationId 리스트 -> Station 정보 리스트로 변환해서 반환
    @Override
    public List<Station> getFavoriteStationsByUserId(String userId) {
        System.out.println("===== GET FAVORITE STATIONS SERVICE 진입 =====");
        System.out.println("[FavoriteService] 요청 userId=" + userId);
        try {
            // 1. favorite 컬렉션에서 userId의 모든 stationId 조회
            List<String> stationIds = favoriteRepository.findByUserId(userId).stream()
                    .map(Favorite::getStationId)
                    .collect(Collectors.toList());

            System.out.println("[FavoriteService] 조회된 stationIds: " + stationIds);

            // 2. stationId 리스트로 charging_stations 컬렉션에서 station 정보 조회
            List<Station> stations = stationService.getStationsByIds(stationIds);

            System.out.println("[FavoriteService] 조회된 stations 개수: " + stations.size());
            return stations;
        } catch (Exception e) {
            System.out.println("[FavoriteService] getFavoriteStationsByUserId 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("즐겨찾기 충전소 정보 조회 중 오류가 발생했습니다.", e);
        }
    }

    // userId와 stationId로 즐겨찾기 확인
    @Override
    public boolean isFavorite(String userId, String stationId) {
        System.out.println("[FavoriteService] isFavorite 호출, userId=" + userId + ", stationId=" + stationId);
        try {
            boolean result = favoriteRepository.findByUserIdAndStationId(userId, stationId)
                    .isPresent();
            System.out.println("[FavoriteService] isFavorite 결과=" + result);
            return result;
        } catch (Exception e) {
            System.out.println("[FavoriteService] isFavorite 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("즐겨찾기 여부 확인 중 오류가 발생했습니다.", e);
        }
    }

    // userId와 stationId로 즐겨찾기 삭제
    @Override
    public void deleteFavorite(String userId, String stationId) {
        System.out.println("===== DELETE FAVORITE SERVICE 진입 =====");
        System.out.println("[FavoriteService] 요청 userId=" + userId + ", stationId=" + stationId);
        try {
            // userId와 stationId로 즐겨찾기 존재 확인
            if (!favoriteRepository.findByUserIdAndStationId(userId, stationId).isPresent()) {
                throw new IllegalArgumentException(
                        "즐겨찾기로 저장되지 않은 역입니다.");
            }

            // 즐겨찾기 삭제
            favoriteRepository.deleteByUserIdAndStationId(userId, stationId);
            System.out.println("[FavoriteService] 즐겨찾기 삭제 완료");
        } catch (Exception e) {
            System.out.println("[FavoriteService] deleteFavorite 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("즐겨찾기 삭제 중 오류가 발생했습니다.", e);
        }
    }

    // userId의 즐겨찾기 개수
    @Override
    public long getFavoriteCount(String userId) {
        System.out.println("[FavoriteService] getFavoriteCount 호출, userId=" + userId);
        try {
            long count = favoriteRepository.countByUserId(userId);
            System.out.println("[FavoriteService] getFavoriteCount 결과=" + count);
            return count;
        } catch (Exception e) {
            System.out.println("[FavoriteService] getFavoriteCount 에러: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("즐겨찾기 개수 조회 중 오류가 발생했습니다.", e);
        }
    }

    // Entity를 Response DTO로 변환
    private FavoriteResponseDto toResponseDto(Favorite favorite) {
        return new FavoriteResponseDto(
                favorite.getId(),
                favorite.getUserId(),
                favorite.getStationId(),
                favorite.getCreatedAt());
    }
}
