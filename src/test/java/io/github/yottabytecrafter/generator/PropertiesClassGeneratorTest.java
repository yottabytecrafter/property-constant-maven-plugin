package io.github.yottabytecrafter.generator;

import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PropertiesClassGeneratorTest {

    @TempDir
    File tempDir;

    private ClassNameStrategy mockStrategy;
    private Properties testProperties;

    @BeforeEach
    void setUp() {
        mockStrategy = mock(ClassNameStrategy.class);
        when(mockStrategy.generateClassName(anyString()))
                .thenReturn("TestClass");

        testProperties = new Properties();
        testProperties.setProperty("test.property", "value");
        testProperties.setProperty("another.property", "another value");
    }

    @Test
    void shouldUseDefaultStrategyWhenStrategyIsNull() throws IOException {
        // Given
        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                "com.example",
                tempDir,
                null,
                null
        );
        File propertiesFile = new File("test.properties");

        // When
        generator.generateClass(propertiesFile, testProperties);

        // Then
        File expectedFile = new File(tempDir, "com/example/TestProperties.java");
        assertTrue(expectedFile.exists());
    }

    @Test
    void shouldGenerateClassFileWithCorrectContent() throws IOException {
        // Given
        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                "com.example",
                tempDir,
                mockStrategy,
                null
        );
        File propertiesFile = new File("test.properties");

        // When
        generator.generateClass(propertiesFile, testProperties);

        // Then
        File expectedFile = new File(tempDir, "com/example/TestClass.java");
        assertTrue(expectedFile.exists());
        String content = new String(Files.readAllBytes(expectedFile.toPath()));

        // Verify file content
        assertTrue(content.contains("package com.example;"));
        assertTrue(content.contains("public final class TestClass"));
        assertTrue(content.contains("@Generated"));
        assertTrue(content.contains("TEST_PROPERTY = \"value\""));
        assertTrue(content.contains("ANOTHER_PROPERTY = \"another value\""));
    }

    @Test
    void shouldCreatePackageDirectories() throws IOException {
        // Given
        String deepPackage = "com.example.deep.package";
        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                deepPackage,
                tempDir,
                mockStrategy,
                null
        );
        File propertiesFile = new File("test.properties");

        // When
        generator.generateClass(propertiesFile, testProperties);

        // Then
        File packageDir = new File(tempDir, "com/example/deep/package");
        assertTrue(packageDir.exists());
        assertTrue(packageDir.isDirectory());
    }

    @Test
    void shouldHandleEmptyProperties() throws IOException {
        // Given
        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                "com.example",
                tempDir,
                mockStrategy,
                null
        );
        File propertiesFile = new File("test.properties");
        Properties emptyProperties = new Properties();

        // When
        generator.generateClass(propertiesFile, emptyProperties);

        // Then
        File expectedFile = new File(tempDir, "com/example/TestClass.java");
        assertTrue(expectedFile.exists());
        String content = new String(Files.readAllBytes(expectedFile.toPath()));
        assertTrue(content.contains("public final class TestClass"));
        assertFalse(content.contains("public static final String"));
    }

    @Test
    void shouldHandleSpecialCharactersInPropertyValues() throws IOException {
        // Given
        Properties specialProperties = new Properties();
        specialProperties.setProperty("test.property", "value\nwith\nnewlines");
        specialProperties.setProperty("quote.property", "value \"with\" quotes");

        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                "com.example",
                tempDir,
                mockStrategy,
                null
        );
        File propertiesFile = new File("test.properties");

        // When
        generator.generateClass(propertiesFile, specialProperties);

        // Then
        File expectedFile = new File(tempDir, "com/example/TestClass.java");
        assertTrue(expectedFile.exists());
        String content = new String(Files.readAllBytes(expectedFile.toPath()));
        assertTrue(content.contains("\"value\\nwith\\nnewlines\""));
        assertTrue(content.contains("\"value \\\"with\\\" quotes\""));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCannotWriteFile() {
        // Given
        File readOnlyDir = mock(File.class);
        when(readOnlyDir.mkdirs()).thenReturn(false);

        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                "com.example",
                readOnlyDir,
                mockStrategy,
                null
        );
        File propertiesFile = new File("test.properties");

        assertThrows(NullPointerException.class,
                () -> generator.generateClass(propertiesFile, testProperties));
    }
}