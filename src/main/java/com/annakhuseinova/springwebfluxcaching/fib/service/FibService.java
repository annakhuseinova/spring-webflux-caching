package com.annakhuseinova.springwebfluxcaching.fib.service;

import org.springframework.stereotype.Service;

@Service
public class FibService {

    public int getFib(int index){
        System.out.println("Calculating fib for" + index);
        return this.fib(index);
    }

    private int fib(int index){
        if (index < 2){
            return index;
        }
        return fib(index - 1) + fib(index - 2);
    }
}
