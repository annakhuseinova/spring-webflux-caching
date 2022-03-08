package com.annakhuseinova.springwebfluxcaching.geo.dto;

import lombok.Data;

@Data
public class Restaurant {

    private String id;
    private String city;
    private double latitude;
    private double longitude;
    private String name;
    private String zip;
}
