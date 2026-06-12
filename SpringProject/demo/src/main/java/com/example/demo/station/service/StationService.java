package com.example.demo.station.service;

import com.example.demo.station.Station;
import com.example.demo.station.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> getAllStations() {
        System.out.println("[StationService] charging_stations 컬렉션에서 전체 station 조회 시작");
        List<Station> stations = stationRepository.findAll();
        System.out.println("[StationService] charging_stations 조회 완료, 개수: " + stations.size());
        return stations;
    }

    // stationId 리스트로 station 정보 조회
    public List<Station> getStationsByIds(List<String> stationIds) {
        System.out.println("[StationService] charging_stations 컬렉션에서 stationIds 기반 조회 시작");
        System.out.println("[StationService] 조회 stationIds: " + stationIds);

        if (stationIds == null || stationIds.isEmpty()) {
            System.out.println("[StationService] stationIds가 비어있습니다.");
            return List.of();
        }

        List<Station> stations = stationRepository.findByIdIn(stationIds);
        System.out.println("[StationService] stationIds 기반 조회 완료, 개수: " + stations.size());
        return stations;
    }
}
