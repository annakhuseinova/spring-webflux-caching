package com.annakhuseinova.springwebfluxcaching;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class SpringWebfluxCachingApplicationTests {

    private RedissonReactiveClient redissonReactiveClient;
    private ReactiveStringRedisTemplate reactiveRedisTemplate;

    @Test
    @RepeatedTest(3)
    void contextLoads() {
        ReactiveValueOperations<String, String> valueOperations = this.reactiveRedisTemplate.opsForValue();

        long before = System.currentTimeMillis();
        Mono<Void> mono = Flux.range(1, 500000)
                .flatMap(integer -> valueOperations.increment("user:1:visit"))
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
        long after = System.currentTimeMillis();
        System.out.println(after - before + " ms");
    }

    @Test
    void redissonTest(){
        RAtomicLongReactive atomicLong = this.redissonReactiveClient.getAtomicLong("user:2:visit");
        long before = System.currentTimeMillis();
        Mono<Void> mono = Flux.range(1, 50000)
                .flatMap(integer -> atomicLong.incrementAndGet())
                .then();
        StepVerifier.create(mono)
                .verifyComplete();
        long after = System.currentTimeMillis();
        System.out.println(after - before + " ms");
    }

}
