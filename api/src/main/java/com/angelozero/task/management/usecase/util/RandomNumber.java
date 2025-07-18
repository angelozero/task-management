package com.angelozero.task.management.usecase.util;

import java.util.Random;

public class RandomNumber {

    public Integer get(Integer bound) {
        return new Random().nextInt(bound) + 1;
    }
}
