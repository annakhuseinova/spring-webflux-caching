package com.annakhuseinova.springwebfluxcaching.geo.service;

import com.annakhuseinova.springwebfluxcaching.geo.dto.GeoLocation;
import com.annakhuseinova.springwebfluxcaching.geo.dto.Restaurant;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Service
public class RestaurantLocatorService {

    private RGeoReactive<Restaurant> geoReactive;
    private RMapReactive<String, GeoLocation> mapReactive;

    public RestaurantLocatorService(RedissonReactiveClient client){
        this.geoReactive = client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
        this.mapReactive = client.getMap("usa", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }

    public Flux<Restaurant> getRestaurants(final String zipcode){
        return this.mapReactive.get(zipcode)
                .map(geoLocation -> GeoSearchArgs.from(geoLocation.getLongitude(), geoLocation.getLatitude()).radius(5, GeoUnit.MILES))
                .flatMap(searchArgs -> this.geoReactive.search(searchArgs))
                .flatMapIterable(Function.identity());
    }
}
