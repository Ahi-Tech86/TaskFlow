package com.ahicode.TextMe.service.generator;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ActivationCodeGeneratorImpl implements ActivationCodeGenerator {

    private final Random random = new Random();

    @Override
    public String generateCode() {
        int number = 1 + random.nextInt(1_000_000);
        return String.format("%06d", number);
    }
}
