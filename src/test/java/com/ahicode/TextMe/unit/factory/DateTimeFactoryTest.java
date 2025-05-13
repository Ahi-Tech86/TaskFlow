package com.ahicode.TextMe.unit.factory;

import com.ahicode.TextMe.service.factory.DateTimeFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DateTimeFactoryTest {

    private final DateTimeFactory factory = new DateTimeFactory();

    @Test
    void toLocalDateTime_WithValidInstant() {
        Instant instant = Instant.now();
        LocalDateTime expectedDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        LocalDateTime actualDateTime = factory.toLocalDateTime(instant);

        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    void toLocalDateTime_WithInvalidInstant() {
        Instant instant = null;

        LocalDateTime actualDateTime = factory.toLocalDateTime(instant);

        assertNull(instant);
    }
}
