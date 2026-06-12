package com.example.demo.favorite.controller;

import com.example.demo.favorite.dto.FavoriteRequestDto;
import com.example.demo.favorite.dto.FavoriteResponseDto;
import com.example.demo.favorite.service.FavoriteService;
import com.example.demo.station.Station;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// REST API Controller
@RestController

// 공통 URL 경로
// 모든 API는 /api/favorites 로 시작
@RequestMapping("/api/favorites")
public class FavoriteController {

    // Service 계층 의존성
    private final FavoriteService favoriteService;

    // 생성자 주입
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /*
     * 즐겨찾기 저장
     *
     * POST /api/favorites
     * 
     * 요청 본문 예시:
     * {
     * "userId": "user123",
     * "stationId": "station456"
     * }
     */
    @PostMapping
    public ResponseEntity<?> saveFavorite(
            @RequestBody FavoriteRequestDto requestDto) {

        System.out.println("===== SAVE FAVORITE API 진입 =====");
        System.out.println("[FavoriteController] 요청 userId=" + requestDto.getUserId() + ", stationId="
                + requestDto.getStationId());
        try {
            FavoriteResponseDto response = favoriteService.saveFavorite(requestDto);
            System.out.println("[FavoriteController] saveFavorite 성공: " + response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("[FavoriteController] saveFavorite 에러: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "즐겨찾기 저장 중 오류가 발생했습니다.");
            error.put("detail", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /*
     * 사용자의 즐겨찾기 역정보 조회
     * userId로 favorite 컬렉션에서 stationId 리스트를 조회한 후,
     * 그 stationId들로 charging_stations 컬렉션에서 station 정보를 조회해서 반환
     *
     * GET /api/favorites/{userId}
     * 
     * 예: GET /api/favorites/user123
     * 응답: [{ id, name, address, lat, lng, chargerType, ... }, ...]
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFavoritesByUserId(
            @PathVariable String userId) {

        System.out.println("===== GET FAVORITES API 진입 =====");
        System.out.println("[FavoriteController] 요청 userId=" + userId);
        try {
            List<Station> response = favoriteService.getFavoriteStationsByUserId(userId);
            System.out.println("[FavoriteController] getFavoritesByUserId 성공, 조회된 station 개수=" + response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("[FavoriteController] getFavoritesByUserId 에러: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "즐겨찾기 충전소 조회 중 오류가 발생했습니다.");
            error.put("detail", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /*
     * 즐겨찾기 여부 확인
     *
     * GET /api/favorites/check?userId=user123&stationId=station456
     */
    @GetMapping("/check")
    public ResponseEntity<?> isFavorite(
            @RequestParam String userId,
            @RequestParam String stationId) {

        System.out.println("===== CHECK FAVORITE API 진입 =====");
        System.out.println("[FavoriteController] 요청 userId=" + userId + ", stationId=" + stationId);
        try {
            boolean found = favoriteService.isFavorite(userId, stationId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("stationId", stationId);
            response.put("isFavorite", found);
            System.out.println("[FavoriteController] isFavorite 결과=" + found);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("[FavoriteController] isFavorite 에러: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "즐겨찾기 여부 확인 중 오류가 발생했습니다.");
            error.put("detail", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /*
     * 즐겨찾기 삭제
     *
     * DELETE /api/favorites?userId=user123&stationId=station456
     */
    @DeleteMapping
    public ResponseEntity<?> deleteFavorite(
            @RequestParam String userId,
            @RequestParam String stationId) {

        System.out.println("===== DELETE FAVORITE API 진입 =====");
        System.out.println("[FavoriteController] 요청 userId=" + userId + ", stationId=" + stationId);
        try {
            favoriteService.deleteFavorite(userId, stationId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "즐겨찾기가 삭제되었습니다.");
            System.out.println("[FavoriteController] deleteFavorite 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("[FavoriteController] deleteFavorite 에러: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "즐겨찾기 삭제 중 오류가 발생했습니다.");
            error.put("detail", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /*
     * 사용자의 즐겨찾기 개수 조회
     *
     * GET /api/favorites/{userId}/count
     * 
     * 예: GET /api/favorites/user123/count
     */
    @GetMapping("/{userId}/count")
    public ResponseEntity<?> getFavoriteCount(
            @PathVariable String userId) {

        System.out.println("===== GET FAVORITE COUNT API 진입 =====");
        System.out.println("[FavoriteController] 요청 userId=" + userId);
        try {
            long count = favoriteService.getFavoriteCount(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("favoriteCount", count);
            System.out.println("[FavoriteController] getFavoriteCount 결과=" + count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("[FavoriteController] getFavoriteCount 에러: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "즐겨찾기 개수 조회 중 오류가 발생했습니다.");
            error.put("detail", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
