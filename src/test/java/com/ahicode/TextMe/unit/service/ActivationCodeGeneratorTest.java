package com.ahicode.TextMe.unit.service;

import com.ahicode.TextMe.service.impl.ActivationCodeGeneratorImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ActivationCodeGeneratorTest {

    private final ActivationCodeGeneratorImpl generator = new ActivationCodeGeneratorImpl();

    @Test
    void testGenerateCode() {
        String code = generator.generateCode();

        assertNotNull(code);
        assertThat(code).isNotBlank();
        assertThat(code).isNotEmpty();
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d+");
        assertThat(Integer.parseInt(code)).isBetween(1, 1_000_000);
    }
}
