package com.justeattakeaway.codechallenge.gameofthree.application.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomNumberGenerator {
    private static final Random random = new Random();

    public int nextNumber() {
        return random.nextInt(50, 100);
    }
}
