package io.github.yottabytecrafter.utils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static Clock clock = Clock.systemDefaultZone();

    private DateTimeUtils() {
    }

    /**
     * Setzt die Clock für Tests. Sollte nur in Tests verwendet werden.
     * @param testClock Die zu verwendende Test-Clock
     */
    public static void setClock(Clock testClock) {
        clock = testClock;
    }

    /**
     * Setzt die Clock zurück auf die System-Clock.
     */
    public static void resetClock() {
        clock = Clock.systemDefaultZone();
    }

    /**
     * Gibt das aktuelle Datum und die Uhrzeit im ISO-Format zurück.
     *
     * @return Aktuelles Datum/Zeit im Format "yyyy-MM-dd'T'HH:mm:ss"
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now(clock).format(ISO_FORMATTER);
    }
}