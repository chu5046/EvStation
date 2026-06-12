package com.example.demo.favorite.repository;

import com.example.demo.favorite.entity.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {

    // userId로 모든 즐겨찾기 조회
    List<Favorite> findByUserId(String userId);

    // userId와 stationId로 즐겨찾기 조회
    Optional<Favorite> findByUserIdAndStationId(String userId, String stationId);

    // userId와 stationId로 즐겨찾기 삭제
    void deleteByUserIdAndStationId(String userId, String stationId);

    // userId의 즐겨찾기 개수 조회
    long countByUserId(String userId);
}
