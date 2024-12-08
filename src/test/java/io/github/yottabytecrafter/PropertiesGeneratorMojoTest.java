package io.github.yottabytecrafter;

import static org.junit.jupiter.api.Assertions.*;

import io.github.yottabytecrafter.source.Source;
import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.util.*;

class PropertiesGeneratorMojoTest {

    @TempDir
    File tempDir;

    private File outputDir;
    private PropertiesGeneratorMojo mojo;
    private List<Source> sources;

    @BeforeEach
    void setUp() throws Exception {
        outputDir = new File(tempDir, "target/generated-sources/java");
        outputDir.mkdirs();

        // Mojo initialisieren
        mojo = new PropertiesGeneratorMojo();
        sources = new ArrayList<>();
        setInternalState(mojo, "outputDirectory", outputDir);
        setInternalState(mojo, "sources", sources);
    }

    @Test
    void shouldGenerateConstantsFromPropertiesFile() throws Exception {
        // Given
        File resourcesDir = new File(tempDir, "src/main/resources");
        resourcesDir.mkdirs();
        File propertiesFile = new File(resourcesDir, "message.properties");

        Properties props = new Properties();
        props.setProperty("test.property", "Test Value");
        props.setProperty("another.property", "Another Value");

        try (FileWriter writer = new FileWriter(propertiesFile)) {
            props.store(writer, null);
        }

        // Konfiguration erstellen
        Source source = new Source();
        source.setPath(propertiesFile.getAbsolutePath());
        source.setTargetPackage("org.test.generated");
        sources.add(source);

        // When
        mojo.execute();

        // Then
        File generatedFile = new File(outputDir, "org/test/generated/MessageProperties.java");
        assertTrue(generatedFile.exists());

        String content;
        try (BufferedReader reader = new BufferedReader(new FileReader(generatedFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            content = sb.toString();
        }

        assertTrue(content.contains("TEST_PROPERTY"));
        assertTrue(content.contains("ANOTHER_PROPERTY"));
    }

    @Test
    void shouldProcessMultipleSourcesWithDifferentPackages() throws Exception {
        // Given
        File resources1 = new File(tempDir, "src/main/resources1");
        File resources2 = new File(tempDir, "src/main/resources2");
        resources1.mkdirs();
        resources2.mkdirs();

        Map<String, String> properties1 = new HashMap<>();
        properties1.put("key1", "value1");
        createPropertiesFile(resources1, "first.properties", properties1);

        Map<String, String> properties2 = new HashMap<>();
        properties2.put("key2", "value2");
        createPropertiesFile(resources2, "second.properties", properties2);

        Source source1 = new Source();
        source1.setPath(resources1.getAbsolutePath());
        source1.setTargetPackage("org.test.first");

        Source source2 = new Source();
        source2.setPath(resources2.getAbsolutePath());
        source2.setTargetPackage("org.test.second");

        sources.add(source1);
        sources.add(source2);

        // When
        mojo.execute();

        // Then
        assertTrue(new File(outputDir, "org/test/first/FirstProperties.java").exists());
        assertTrue(new File(outputDir, "org/test/second/SecondProperties.java").exists());
    }

    @Test
    void shouldUseCustomClassNameStrategy() throws Exception {
        // Given
        setInternalState(mojo, "classNameStrategyClass", CustomTestStrategy.class.getName());

        File resourcesDir = new File(tempDir, "src/main/resources");
        resourcesDir.mkdirs();
        File propertiesFile = new File(resourcesDir, "test.properties");
        Map<String, String> properties = new HashMap<>();
        properties.put("key", "value");
        createPropertiesFile(resourcesDir, "test.properties", properties);

        Source source = new Source();
        source.setPath(propertiesFile.getAbsolutePath());
        source.setTargetPackage("org.test.generated");
        sources.add(source);

        // When
        mojo.execute();

        // Then
        File generatedFile = new File(outputDir, "org/test/generated/CustomNameProperties.java");
        assertTrue(generatedFile.exists());
    }

    // Hilfsmethoden bleiben gleich
    private void createPropertiesFile(File directory, String fileName, Map<String, String> properties) throws IOException {
        File file = new File(directory, fileName);
        Properties props = new Properties();
        props.putAll(properties);
        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, null);
        }
    }

    private void setInternalState(Object target, String field, Object value) {
        try {
            java.lang.reflect.Field fieldObject = target.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class CustomTestStrategy implements ClassNameStrategy {
        @Override
        public String generateClassName(String propertyFile) {
            return "CustomNameProperties";
        }
    }
}
