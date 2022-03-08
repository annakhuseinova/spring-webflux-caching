package com.annakhuseinova.springwebfluxcaching.geo.service;

import com.annakhuseinova.springwebfluxcaching.geo.dto.GeoLocation;
import com.annakhuseinova.springwebfluxcaching.geo.dto.Restaurant;
import com.annakhuseinova.springwebfluxcaching.geo.util.RestaurantUtil;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Service
public class DataSetupService implements CommandLineRunner {

    private RGeoReactive<Restaurant> geoReactive;
    private RMapReactive<String, GeoLocation> mapReactive;
    @Autowired
    private RedissonReactiveClient redissonReactiveClient;

    @Override
    public void run(String... args) throws Exception {
        this.geoReactive = this.redissonReactiveClient.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
        this.mapReactive = this.redissonReactiveClient.getMap("usa", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
        Flux.fromIterable(RestaurantUtil.getRestaurants())
                .flatMap(restaurant -> this.geoReactive.add(restaurant.getLongitude(), restaurant.getLatitude(), restaurant)
                        .thenReturn(restaurant))
                .flatMap(restaurant -> this.mapReactive.fastPut(restaurant.getZip(), GeoLocation.of(restaurant.getLongitude(), restaurant.getLatitude())))
                .doFinally(s -> System.out.println("Restaurants added " + s))
                .subscribe();
    }

    public Flux<Restaurant> getRestaurants(final String zipcode){
        return this.mapReactive.get(zipcode)
                .map(geoLocation -> GeoSearchArgs.from(geoLocation.getLongitude(), geoLocation.getLatitude()).radius(5, GeoUnit.MILES))
                .flatMap(geoSearchArgs -> this.geoReactive.search(geoSearchArgs))
                .flatMapIterable(Function.identity());
    }
}
