package io.github.yottabytecrafter.generator;

import io.github.yottabytecrafter.builder.ClassBuilder;
import io.github.yottabytecrafter.strategy.DefaultClassNameStrategy;
import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap; // Preserve insertion order for predictable output
import java.util.Map;

public class PropertiesClassGeneratorTest {

    private ClassNameStrategy classNameStrategy;
    private static final String TEST_PACKAGE = "com.example.test";
    private static final String PLUGIN_VERSION = "test-version";

    @TempDir
    Path tempDir; // JUnit 5 temporary directory

    @BeforeEach
    void setUp() {
        classNameStrategy = new DefaultClassNameStrategy(); // Using default strategy
    }

    // Helper to normalize string for comparison by removing CR and collapsing multiple spaces/newlines
    private String normalizeCode(String code) {
        if (code == null) return "";
        return code.replace("\r\n", "\n") // Normalize line endings
                   .replaceAll("\\s+\n", "\n") // Remove trailing spaces before newlines
                   .replaceAll("\n\\s+", "\n") // Remove leading spaces after newlines
                   .replaceAll("\\s+", " ")   // Collapse multiple whitespace chars to a single space
                   .trim();
    }
    
    // More robust helper for checking code snippets, ignoring minor whitespace variations
    private void assertCodeContains(String actualCode, String expectedSnippet) {
        String normalizedActual = normalizeCode(actualCode);
        String normalizedExpected = normalizeCode(expectedSnippet);
        assertTrue(normalizedActual.contains(normalizedExpected),
                "Expected snippet not found in actual code.\n--- Expected (normalized) ---\n" + normalizedExpected + "\n--- Actual (normalized) ---\n" + normalizedActual + "\n--- Original Actual Code ---\n" + actualCode);
    }
    
    private void assertCodeDoesNotContain(String actualCode, String unexpectedSnippet) {
        String normalizedActual = normalizeCode(actualCode);
        String normalizedUnexpected = normalizeCode(unexpectedSnippet);
        assertFalse(normalizedActual.contains(normalizedUnexpected),
                "Unexpected snippet found in actual code.\n--- Unexpected (normalized) ---\n" + normalizedUnexpected + "\n--- Actual (normalized) ---\n" + normalizedActual);
    }


    @Test
    void testClassBuilder_BasicTwoLanguages() {
        ClassBuilder builder = new ClassBuilder("TestProperties", TEST_PACKAGE, PLUGIN_VERSION);
        builder.addGenerated("test_*.properties")
               .makeClassFinal()
               .addPrivateConstructor();

        Map<String, String> greetingTranslations = new LinkedHashMap<>();
        greetingTranslations.put("en", "Hello");
        greetingTranslations.put("de", "Hallo");
        builder.addLocalizedConstant("greeting", "GREETING", greetingTranslations);

        Map<String, String> nameTranslations = new LinkedHashMap<>();
        nameTranslations.put("en", "World");
        nameTranslations.put("de", "Welt");
        builder.addLocalizedConstant("name.key", "NAME_KEY", nameTranslations); 

        String actualCode = builder.build();

        assertCodeContains(actualCode, "package com.example.test;");
        assertCodeContains(actualCode, "import java.util.Map;");
        assertCodeContains(actualCode, "import java.util.HashMap;");
        assertCodeContains(actualCode, "import javax.annotation.Generated;");
        assertCodeContains(actualCode, "public final class TestProperties {");
        assertCodeContains(actualCode, "private TestProperties() {");
        assertCodeContains(actualCode, "public static final java.util.Map<String, String> GREETING;");
        assertCodeContains(actualCode, "public static final java.util.Map<String, String> NAME_KEY;");
        assertCodeContains(actualCode, "static {");
        assertCodeContains(actualCode, "GREETING = new java.util.HashMap<>();");
        assertCodeContains(actualCode, "GREETING.put(\"en\", \"Hello\");");
        assertCodeContains(actualCode, "GREETING.put(\"de\", \"Hallo\");");
        assertCodeContains(actualCode, "NAME_KEY = new java.util.HashMap<>();");
        assertCodeContains(actualCode, "NAME_KEY.put(\"en\", \"World\");");
        assertCodeContains(actualCode, "NAME_KEY.put(\"de\", \"Welt\");");
        assertCodeContains(actualCode, "}"); 
        assertCodeContains(actualCode, "}"); 
        
        assertCodeContains(actualCode, "Localized constant for property key: 'greeting'.");
        assertCodeContains(actualCode, "Localized constant for property key: 'name.key'.");

    }

    @Test
    void testClassBuilder_PropertyMissingInOneLanguage() {
        ClassBuilder builder = new ClassBuilder("MissingProperties", TEST_PACKAGE, PLUGIN_VERSION);
        builder.addGenerated("missing_*.properties")
               .makeClassFinal()
               .addPrivateConstructor();

        Map<String, String> onlyInEnglishTranslations = new LinkedHashMap<>();
        onlyInEnglishTranslations.put("en", "English Only");
        builder.addLocalizedConstant("only.in.english", "ONLY_IN_ENGLISH", onlyInEnglishTranslations);

        String actualCode = builder.build();

        assertCodeContains(actualCode, "public static final java.util.Map<String, String> ONLY_IN_ENGLISH;");
        assertCodeContains(actualCode, "static {");
        assertCodeContains(actualCode, "ONLY_IN_ENGLISH = new java.util.HashMap<>();");
        assertCodeContains(actualCode, "ONLY_IN_ENGLISH.put(\"en\", \"English Only\");");
        assertCodeDoesNotContain(actualCode, "ONLY_IN_ENGLISH.put(\"de\",");
    }

    @Test
    void testClassBuilder_SpecialCharactersAndLanguageCode() {
        ClassBuilder builder = new ClassBuilder("SpecialProperties", TEST_PACKAGE, PLUGIN_VERSION);
        builder.addGenerated("special_*.properties")
               .makeClassFinal()
               .addPrivateConstructor();

        Map<String, String> appTitleTranslations = new LinkedHashMap<>();
        appTitleTranslations.put("en", "My App \"Test\""); 
        appTitleTranslations.put("fr-CA", "Mon App \"Test\""); 
        builder.addLocalizedConstant("app.title", "APP_TITLE", appTitleTranslations);
        
        Map<String, String> keyWithHyphenTranslations = new LinkedHashMap<>();
        keyWithHyphenTranslations.put("en", "Value with newline\nand tab\t"); 
        keyWithHyphenTranslations.put("fr-CA", "Valeur");
        builder.addLocalizedConstant("key.with-hyphen", "KEY_WITH_HYPHEN", keyWithHyphenTranslations);


        String actualCode = builder.build();

        assertCodeContains(actualCode, "public static final java.util.Map<String, String> APP_TITLE;");
        assertCodeContains(actualCode, "APP_TITLE.put(\"en\", \"My App \\\"Test\\\"\");"); 
        assertCodeContains(actualCode, "APP_TITLE.put(\"fr-CA\", \"Mon App \\\"Test\\\"\");"); 
        
        assertCodeContains(actualCode, "public static final java.util.Map<String, String> KEY_WITH_HYPHEN;");
        assertCodeContains(actualCode, "KEY_WITH_HYPHEN.put(\"en\", \"Value with newline\\nand tab\\t\");"); 
        assertCodeContains(actualCode, "KEY_WITH_HYPHEN.put(\"fr-CA\", \"Valeur\");");
    }

    @Test
    void testClassBuilder_EmptyProperties() {
        ClassBuilder builder = new ClassBuilder("EmptyProperties", TEST_PACKAGE, PLUGIN_VERSION);
        builder.addGenerated("empty_*.properties")
               .makeClassFinal()
               .addPrivateConstructor();

        String actualCode = builder.build();

        assertCodeDoesNotContain(actualCode, "static {"); 
        assertCodeDoesNotContain(actualCode, "public static final java.util.Map<String, String>");
        assertCodeContains(actualCode, "public final class EmptyProperties"); 
    }
    
    @Test
    void testClassBuilder_Imports() {
        ClassBuilder builder = new ClassBuilder("ImportTestProperties", TEST_PACKAGE, PLUGIN_VERSION);
        builder.addGenerated("imports_*.properties")
               .makeClassFinal()
               .addPrivateConstructor();

        Map<String, String> testTranslations = new LinkedHashMap<>();
        testTranslations.put("en", "Test");
        builder.addLocalizedConstant("test.key", "TEST_KEY", testTranslations); 

        String actualCode = builder.build();

        assertCodeContains(actualCode, "import java.util.Map;");
        assertCodeContains(actualCode, "import java.util.HashMap;");
        assertCodeContains(actualCode, "import javax.annotation.Generated;");
    }

    @Test
    void testPropertiesClassGenerator_BasicTwoLanguages() throws IOException {
        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                TEST_PACKAGE,
                tempDir.toFile(),
                classNameStrategy, 
                PLUGIN_VERSION
        );

        Map<String, Map<String, String>> propertyTranslations = new LinkedHashMap<>();
        Map<String, String> greetingTranslations = new LinkedHashMap<>();
        greetingTranslations.put("en", "Hello");
        greetingTranslations.put("de", "Hallo");
        propertyTranslations.put("greeting", greetingTranslations);

        Map<String, String> nameTranslations = new LinkedHashMap<>();
        nameTranslations.put("en", "World");
        nameTranslations.put("de", "Welt");
        propertyTranslations.put("name", nameTranslations); 

        generator.generateClass("Test", propertyTranslations); 

        File expectedFile = tempDir.resolve(TEST_PACKAGE.replace('.', '/')).resolve("TestProperties.java").toFile();
        assertTrue(expectedFile.exists(), "Generated file should exist: " + expectedFile.getAbsolutePath());

        String actualCode = Files.readString(expectedFile.toPath());

        assertCodeContains(actualCode, "package com.example.test;");
        assertCodeContains(actualCode, "public final class TestProperties {");
        assertCodeContains(actualCode, "public static final java.util.Map<String, String> GREETING;");
        assertCodeContains(actualCode, "GREETING.put(\"en\", \"Hello\");");
        assertCodeContains(actualCode, "GREETING.put(\"de\", \"Hallo\");");
        assertCodeContains(actualCode, "public static final java.util.Map<String, String> NAME;"); 
        assertCodeContains(actualCode, "NAME.put(\"en\", \"World\");");
        assertCodeContains(actualCode, "NAME.put(\"de\", \"Welt\");");
        assertCodeContains(actualCode, "@Generated(");
        assertCodeContains(actualCode, "value = \"io.github.yottabytecrafter.PropertiesGeneratorMojo\"");
        assertCodeContains(actualCode, "comments = \"Generated from Test_*.properties, version: test-version"); 
        assertCodeContains(actualCode, "Localized constant for property key: 'greeting'.");
        assertCodeContains(actualCode, "Localized constant for property key: 'name'.");
    }
    
    @Test
    void testPropertiesClassGenerator_EmptyProperties() throws IOException {
        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                TEST_PACKAGE,
                tempDir.toFile(),
                classNameStrategy,
                PLUGIN_VERSION
        );

        Map<String, Map<String, String>> propertyTranslations = Collections.emptyMap();

        generator.generateClass("Empty", propertyTranslations); 

        File expectedFile = tempDir.resolve(TEST_PACKAGE.replace('.', '/')).resolve("EmptyProperties.java").toFile();
        assertTrue(expectedFile.exists(), "Generated file should exist for empty properties.");

        String actualCode = Files.readString(expectedFile.toPath());
        
        assertCodeContains(actualCode, "public final class EmptyProperties {");
        assertCodeDoesNotContain(actualCode, "static {"); 
        assertCodeDoesNotContain(actualCode, "public static final java.util.Map<String, String>");
        assertCodeContains(actualCode, "@Generated(");
        assertCodeContains(actualCode, "comments = \"Generated from Empty_*.properties, version: test-version");
    }
}
