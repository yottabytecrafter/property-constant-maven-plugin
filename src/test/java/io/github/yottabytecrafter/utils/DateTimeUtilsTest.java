package io.github.yottabytecrafter.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

    @AfterEach
    void cleanup() {
        DateTimeUtils.resetClock();
    }

    @Test
    void getCurrentDateTime_shouldReturnFixedTime() {
        Instant fixedInstant = Instant.parse("2024-01-15T10:15:30.00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        DateTimeUtils.setClock(fixedClock);

        String result = DateTimeUtils.getCurrentDateTime();

        assertEquals("2024-01-15T10:15:30", result);
    }

    @Test
    void getCurrentDateTime_shouldHandleDifferentTimeZones() {
        Instant fixedInstant = Instant.parse("2024-01-15T10:15:30.00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("Europe/Berlin"));
        DateTimeUtils.setClock(fixedClock);

        String result = DateTimeUtils.getCurrentDateTime();

        assertEquals("2024-01-15T11:15:30", result); // Berlin ist UTC+1
    }

    @Test
    void getCurrentDateTime_shouldFormatCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        Instant fixedInstant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
        DateTimeUtils.setClock(fixedClock);

        String result = DateTimeUtils.getCurrentDateTime();

        assertEquals("2024-12-31T23:59:59", result);
    }
}