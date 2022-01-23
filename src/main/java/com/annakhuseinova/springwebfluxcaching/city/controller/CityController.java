package com.annakhuseinova.springwebfluxcaching.city.controller;

import com.annakhuseinova.springwebfluxcaching.city.City;
import com.annakhuseinova.springwebfluxcaching.city.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("city")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("{zipCode}")
    public Mono<City> getCity(@PathVariable String zipCode){
        return this.cityService.getCity(zipCode);
    }
}
