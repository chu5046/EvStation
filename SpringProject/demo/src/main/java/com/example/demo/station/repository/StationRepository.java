package com.example.demo.station.repository;

import com.example.demo.station.Station;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends MongoRepository<Station, String> {

    // stationId 리스트로 station 정보 조회
    List<Station> findByIdIn(List<String> stationIds);
}
