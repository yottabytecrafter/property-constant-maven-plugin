package io.github.yottabytecrafter.builder;

import io.github.yottabytecrafter.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class ClassBuilderTest {

        @Test
        void shouldBuildMinimalClass() {
            // Given
            ClassBuilder builder = new ClassBuilder("TestClass", "com.example", null);

            // When
            String result = builder
                    .makeClassFinal()
                    .build();

            // Then
            String expected =
                    "package com.example;\n\n" +
                            "import javax.annotation.Generated;\n\n" +
                            "public final class TestClass {\n\n" +
                            "}\n";
            assertEquals(expected, result);
        }

        @Test
        void shouldBuildCompleteClass() {
            // Given
            ClassBuilder builder = new ClassBuilder("Constants", "com.example", "1.8.0");

            // Set fixed time for @Generated annotation
            Instant fixedInstant = Instant.parse("2024-01-15T10:15:30.00Z");
            Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
            DateTimeUtils.setClock(fixedClock);

            String javaVersion = System.getProperty("java.version");
            String javaVendor = System.getProperty("java.vendor");

            // When
            String result = builder
                    .addGenerated("config.properties")
                    .makeClassFinal()
                    .addPrivateConstructor()
                    .addConstant("PROPERTY_ONE", "value1")
                    .addConstant("PROPERTY_TWO", "value2")
                    .build();


            // Then
            String expected =
                    String.format(
                            "package com.example;\n\n" +
                            "import javax.annotation.Generated;\n\n" +
                            "@Generated(\n" +
                            "    value = \"io.github.yottabytecrafter.PropertiesGeneratorMojo\",\n" +
                            "    date = \"2024-01-15T10:15:30\",\n" +
                            "    comments = \"Generated from config.properties, version: 1.8.0, environment: Java %s (%s)\"\n" +
                            ")\n" +
                            "public final class Constants {\n\n" +
                            "    private Constants() {\n" +
                            "        // Prevent instantiation\n" +
                            "    }\n\n" +
                            "    public static final String PROPERTY_ONE = \"value1\";\n" +
                            "    public static final String PROPERTY_TWO = \"value2\";\n" +
                            "}\n"
                    , javaVersion, javaVendor);
            assertEquals(expected, result);

            // Cleanup
            DateTimeUtils.resetClock();
        }

        @Test
        void shouldHandleSpecialCharactersInConstantValues() {
            // Given
            ClassBuilder builder = new ClassBuilder("SpecialConstants", "com.example", null);

            // When
            String result = builder
                    .makeClassFinal()
                    .addConstant("NEW_LINE", "line1\nline2")
                    .addConstant("QUOTES", "\"quoted text\"")
                    .addConstant("UNICODE", "Hello • World")
                    .build();

            // Then
            String expected =
                    "package com.example;\n\n" +
                            "import javax.annotation.Generated;\n\n" +
                            "public final class SpecialConstants {\n\n" +
                            "    public static final String NEW_LINE = \"line1\nline2\";\n" +
                            "    public static final String QUOTES = \"\"quoted text\"\";\n" +
                            "    public static final String UNICODE = \"Hello • World\";\n" +
                            "}\n";
            assertEquals(expected, result);
        }

        @Test
        void shouldBuildClassWithOnlyConstants() {
            // Given
            ClassBuilder builder = new ClassBuilder("OnlyConstants", "com.example", null);

            // When
            String result = builder
                    .makeClassFinal()
                    .addConstant("CONSTANT_ONE", "1")
                    .addConstant("CONSTANT_TWO", "2")
                    .addConstant("CONSTANT_THREE", "3")
                    .build();

            // Then
            String expected =
                    "package com.example;\n\n" +
                            "import javax.annotation.Generated;\n\n" +
                            "public final class OnlyConstants {\n\n" +
                            "    public static final String CONSTANT_ONE = \"1\";\n" +
                            "    public static final String CONSTANT_TWO = \"2\";\n" +
                            "    public static final String CONSTANT_THREE = \"3\";\n" +
                            "}\n";
            assertEquals(expected, result);
        }
}