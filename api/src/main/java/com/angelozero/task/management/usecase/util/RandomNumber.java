package com.angelozero.task.management.usecase.util;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomNumber {

    public Integer get(Integer bound) {
        return new Random().nextInt(bound) + 1;
    }
}
