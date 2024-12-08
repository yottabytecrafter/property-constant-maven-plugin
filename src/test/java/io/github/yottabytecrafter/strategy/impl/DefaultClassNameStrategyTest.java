package io.github.yottabytecrafter.strategy.impl;

import io.github.yottabytecrafter.strategy.DefaultClassNameStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultClassNameStrategyTest {

    private final DefaultClassNameStrategy strategy = new DefaultClassNameStrategy();

    @Test
    void shouldRemovePropertiesExtension() {
        String result = strategy.generateClassName("config.properties");
        assertEquals("ConfigProperties", result);
    }

    @Test
    void shouldHandleFilenameWithoutExtension() {
        String result = strategy.generateClassName("config");
        assertEquals("ConfigProperties", result);
    }

    @Test
    void shouldCapitalizeFirstLetter() {
        String result = strategy.generateClassName("myConfig.properties");
        assertEquals("MyConfigProperties", result);
    }

    @Test
    void shouldPreserveExistingCapitalization() {
        String result = strategy.generateClassName("MyConfig.properties");
        assertEquals("MyConfigProperties", result);
    }

    @Test
    void shouldHandleMultipleDots() {
        String result = strategy.generateClassName("my.config.properties");
        assertEquals("MyConfigProperties", result);
    }

    @Test
    void shouldThrowExceptionForNullFilename() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> strategy.generateClassName(null)
        );
        assertEquals("Filename cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyFilename() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> strategy.generateClassName("")
        );
        assertEquals("Filename cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldHandleSingleCharacterFilename() {
        String result = strategy.generateClassName("a");
        assertEquals("AProperties", result);
    }

    @Test
    void shouldHandleSingleCharacterWithExtension() {
        String result = strategy.generateClassName("a.properties");
        assertEquals("AProperties", result);
    }

}