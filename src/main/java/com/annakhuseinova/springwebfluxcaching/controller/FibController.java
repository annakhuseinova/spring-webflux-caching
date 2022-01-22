package com.annakhuseinova.springwebfluxcaching.controller;

import com.annakhuseinova.springwebfluxcaching.fib.service.FibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("fib")
public class FibController {

    @Autowired
    private FibService fibService;

    @GetMapping("{index}")
    public Mono<Integer> getFib(@PathVariable int index){
        return Mono.fromSupplier(()-> this.fibService.getFib(index));
    }
}