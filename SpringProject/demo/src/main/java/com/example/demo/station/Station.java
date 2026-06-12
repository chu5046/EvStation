package com.example.demo.station;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "charging_stations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Station {

    @Id
    private String id;

    private String name;
    private String address;
    private double lat;
    private double lng;
    private String chargerType;
    private int totalChargers;
    private int availableChargers;
    private String power;
    private String operator;
    private String status;
    private int pricePerKwh;
}
