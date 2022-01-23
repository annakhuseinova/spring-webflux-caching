package com.annakhuseinova.springwebfluxcaching.city;

import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service
public class CityService {

    private RMapCacheReactive<String, City> cityMap;
    @Autowired
    private CityClient cityClient;

    public CityService(RedissonReactiveClient client){
        this.cityMap = client.getMapCache("city", new TypedJsonJacksonCodec(String.class, City.class));
    }

    public Mono<City> getCity(final String zipCode){
        return this.cityMap.get(zipCode)
                        .switchIfEmpty(this.cityClient.getCity(zipCode)
                        .flatMap(city -> this.cityMap.fastPut(zipCode, city, 10, TimeUnit.SECONDS)
                        .thenReturn(city)));
    }
}
