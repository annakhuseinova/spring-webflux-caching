package com.annakhuseinova.springwebfluxcaching.city;

import lombok.Data;

@Data
public class City {

    private String zip;
    private String city;
    private String stateName;
    private int temperature;
}
