package com.example.demo.station.controller;

import com.example.demo.station.Station;
import com.example.demo.station.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<Station> getAllStations() {
        System.out.println("[StationController] GET /api/stations 호출");
        List<Station> stations = stationService.getAllStations();
        System.out.println("[StationController] 조회된 station 개수: " + stations.size());
        return stations;
    }

    /*
     * stationId 리스트로 station 정보 조회
     * 
     * POST /api/stations/by-ids
     * 
     * 요청 본문 예시:
     * ["station1", "station2", "station3"]
     */
    @PostMapping("/by-ids")
    public List<Station> getStationsByIds(@RequestBody List<String> stationIds) {
        System.out.println("[StationController] POST /api/stations/by-ids 호출");
        List<Station> stations = stationService.getStationsByIds(stationIds);
        System.out.println("[StationController] 조회된 station 개수: " + stations.size());
        return stations;
    }
}
