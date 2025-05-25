# Property Constant Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.yottabytecrafter/property-constant-maven-plugin.svg)](https://search.maven.org/artifact/io.github.yottabytecrafter/property-constant-maven-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=yottabytecrafter_property-constant-maven-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=yottabytecrafter_property-constant-maven-plugin)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=yottabytecrafter_property-constant-maven-plugin&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=yottabytecrafter_property-constant-maven-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=yottabytecrafter_property-constant-maven-plugin&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=yottabytecrafter_property-constant-maven-plugin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=yottabytecrafter_property-constant-maven-plugin&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=yottabytecrafter_property-constant-maven-plugin)

## Description

The Property Constant Maven Plugin is a tool designed to enhance Java development by automatically generating Java constant classes from property files. It simplifies the management of properties by creating corresponding Java classes containing static string constants for each key in the property files. This reduces the risk of runtime errors due to typos in property keys and improves code maintainability.

The plugin scans specified directories for `.properties` files during the build process and generates corresponding Java classes. Each property key is converted into a well-defined constant within the generated class, ensuring type safety and compile-time error checking.

## Features

*   **Automatic Java Class Generation:** Automatically generates Java classes from `.properties` files.
*   **Compile-Time Safety:** Ensures that property keys are valid identifiers at compile time.
*   **Robust Constant Naming:** Converts property keys into Java-compliant constant names, including handling for leading digits (by prefixing with an underscore) and sanitizing invalid characters.
*   **UTF-8 Support for Properties Files:** Properly reads property files using a specified encoding (defaults to UTF-8) via the `propertiesEncoding` parameter, ensuring correct handling of non-ASCII characters.
*   **Generated Javadoc:** Automatically generates class-level Javadoc (including the source properties file name or base name for i18n groups) and constant-level Javadoc (including the original property key) for better usability and documentation.
*   **Multi-language Support (i18n):** Generates Java classes with `Map<String, String>` constants for localized properties, allowing easy access to translations based on language codes.
*   **Integration Tests:** Includes comprehensive integration tests to ensure reliability.
*   **Maven Integration:** Seamlessly integrates with the Maven build lifecycle.

## Usage

To use the Property Constant Maven Plugin, add it to your project's `pom.xml` in the `<build>` section:

```xml

<build>
    <plugins>
        <plugin>
            <groupId>io.github.yottabytecrafter</groupId>
            <artifactId>property-constant-maven-plugin</artifactId>
            <version>0.1</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate-properties</goal>
                    </goals>
                    <phase>generate-sources</phase>
                </execution>
            </executions>
            <configuration>
                <sources>
                    <source>
                        <!-- Path to a single *.properties File (legacy mode) -->
                        <!-- <path>${project.basedir}/src/main/resources/config.properties</path> -->
                        <!-- <targetPackage>com.example.legacyconfig</targetPackage> -->
                    </source>
                    <source>
                        <!-- Path to a directory with *.properties Files (for non-i18n properties) -->
                        <!-- <path>${project.basedir}/src/main/resources/settings</path> -->
                        <!-- <targetPackage>com.example.settings</targetPackage> -->
                    </source>
                    <source>
                        <!-- Path to a directory for localized properties (i18n) -->
                        <!-- e.g., src/main/resources/i18n might contain: -->
                        <!-- messages_en.properties, messages_de.properties, labels_en_US.properties, etc. -->
                        <path>${project.basedir}/src/main/resources/i18n</path>
                        <targetPackage>com.example.i18n</targetPackage>
                    </source>
                </sources>
                <!-- Optional: output directory for generated Java classes -->
                <outputDirectory>${project.build.directory}/generated-sources/java</outputDirectory>
                <!-- Optional: Specify encoding for reading .properties files -->
                <propertiesEncoding>UTF-8</propertiesEncoding> <!-- Default is UTF-8 -->
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Configuration Options

| Parameter                | Description                                                                                                       | Default Value                                                  |
| :----------------------- | :---------------------------------------------------------------------------------------------------------------- |:---------------------------------------------------------------|
| `sources`                | A list of source configurations. Each `<source>` element defines a set of properties to process.                 | (Required)                                                     |
| `source`/`path`          | The path to a directory containing property files or to a single property file. **For multi-language support, this should be a directory.** | (Required within `source`)                                     |
| `source`/`targetPackage` | The Java package for the generated class(es) from this source.                                                   | (Required within `source`)                                     |
| `outputDirectory`        | The root directory where generated Java classes will be written.                                                  | `${project.build.directory}/generated-sources/java`            |
| `propertiesEncoding`     | The character encoding used to read the `.properties` files.                                                      | `UTF-8`                                                        |
| `classNameStrategyClass` | Optional: Fully qualified class name of a custom `ClassNameStrategy` implementation. This allows you to define your own logic for how Java class names are generated from property file names (or base names for i18n). | `io.github.yottabytecrafter.strategy.DefaultClassNameStrategy` |

### Property File Naming Convention for Multi-language Support

To leverage the multi-language support, your property files must follow a specific naming convention:

`basename_language.properties`

*   **`basename`**: This is the base name for a group of related translation files. It's used to group them together and typically determines the generated Java class name (e.g., `messages` could become `MessagesProperties.java`).
*   **`language`**: This is the language code. It generally follows:
    *   ISO 639-1 two-letter codes (e.g., `en` for English, `de` for German, `fr` for French).
    *   Or, it can include a region/country variant (e.g., `en_US` for English - United States, `fr_CA` for French - Canada, `pt_BR` for Portuguese - Brazil). The plugin extracts this full string as the language key.
*   **`.properties`**: The standard properties file extension.

**Example Directory Structure (`src/main/resources/i18n/`):**

```
src/
└── main/
    └── resources/
        └── i18n/
            ├── messages_en.properties
            ├── messages_de.properties
            ├── messages_fr_CA.properties
            ├── labels_en.properties
            └── labels_es.properties
```

In this example:
*   `messages_en.properties`, `messages_de.properties`, `messages_fr_CA.properties` form a group with `basename` "messages". This will likely generate a `MessagesProperties.java` (depending on the class name strategy).
*   `labels_en.properties`, `labels_es.properties` form another group with `basename` "labels", likely generating `LabelsProperties.java`.

If the `<path>` in a `<source>` configuration points to a single file (e.g., `config.properties`), the plugin will process it in a legacy, non-i18n mode, generating simple string constants. If the path points to a directory, it will attempt to find files matching the `basename_language.properties` pattern for i18n processing.

### Custom Class Name Strategy

Users can provide their own logic for generating class names from property file names by implementing the `io.github.yottabytecrafter.strategy.ClassNameStrategy` interface.

**1. Create your custom strategy implementation:**

```java
package com.example.myproject;

import io.github.yottabytecrafter.strategy.ClassNameStrategy;
// No need to import java.io.File for this specific example,
// but it might be needed for more complex strategies.

public class MyCustomStrategy implements ClassNameStrategy {
    @Override
    public String generateClassName(String name) {
        // 'name' will be the basename (e.g., "messages" from "messages_en.properties")
        // when processing i18n property groups.
        // If processing a single, non-i18n file, 'name' will be the full file name (e.g., "config.properties").
        
        String baseNameForClass = name;
        if (name.endsWith(".properties")) { // Handle legacy single file case
            baseNameForClass = name.substring(0, name.length() - ".properties".length());
        }
        
        // Example: Capitalize the first letter and append "Bundle"
        String sanitizedBaseName = baseNameForClass.replaceAll("[^a-zA-Z0-9_]", "");
        if (sanitizedBaseName.isEmpty()) {
            return "DefaultBundle"; 
        }
        // Capitalize first letter
        sanitizedBaseName = Character.toUpperCase(sanitizedBaseName.charAt(0)) + sanitizedBaseName.substring(1);
        
        return sanitizedBaseName + "Bundle"; // e.g., "messages" -> "MessagesBundle"
    }
}
```

**2. Configure the plugin in your `pom.xml`:**

```xml
<plugin>
    <groupId>io.github.yottabytecrafter</groupId>
    <artifactId>property-constant-maven-plugin</artifactId>
    <version>YOUR_PLUGIN_VERSION</version> <!-- Replace with the actual plugin version -->
    <executions>
        <execution>
            <goals>
                <goal>generate-properties</goal>
            </goals>
            <configuration>
                <sources>
                    <source>
                        <path>src/main/resources/myconfig.properties</path>
                        <targetPackage>com.example.myproject.generated</targetPackage>
                    </source>
                </sources>
                <classNameStrategyClass>com.example.myproject.MyCustomStrategy</classNameStrategyClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Important Note on Classpath:**
The custom strategy class (e.g., `com.example.myproject.MyCustomStrategy`) must be compiled and available on the classpath when the plugin executes (typically during the `generate-sources` phase).
*   If your custom strategy is in the same Maven module where you are using the plugin: Maven's default lifecycle compiles `src/main/java` during the `compile` phase, which is *after* `generate-sources`. Thus, the custom strategy class will not be found. To address this, you would need to compile the custom strategy class before the `generate-sources` phase. This can be achieved by:
    *   Placing the custom strategy in a separate utility module that your current project depends on. This is the recommended approach as it clearly separates concerns.
    *   Customizing your build lifecycle to compile these specific classes earlier (this is a more advanced Maven customization).
*   The plugin uses the Thread Context ClassLoader to load the custom strategy, which helps if the class is available on the project's classpath at the time of execution.

## Example

Given a property file `src/main/resources/message.properties` with the following content:

```properties
greeting=Hello, World!
farewell=Goodbye
```

The plugin will generate a Java class `io.github.yottabytecrafter.config.MessageProperties` (if `io.github.yottabytecrafter.config` is the `targetPackage`):

```java
package io.github.yottabytecrafter.config;

import javax.annotation.Generated;

/**
 * Contains constants generated from the properties file: 'message.properties'.
 * <p>
 * This class is automatically generated by the property-constant-maven-plugin.
 * Do not modify this file directly.
 */
@Generated(
    value = "io.github.yottabytecrafter.PropertiesGeneratorMojo",
    date = "YYYY-MM-DDTHH:mm:ssZ", // Example date
    comments = "Generated from message.properties, version: 0.1, environment: Java X.Y.Z (Vendor)" // Example comment
)
public final class MessageProperties { // Assuming DefaultClassNameStrategy for a single file "message.properties"

    private MessageProperties() {
        // Prevent instantiation
    }

    /**
     * Constant for property key: 'greeting'.
     */
    public static final String GREETING = "Hello, World!";

    /**
     * Constant for property key: 'farewell'.
     */
    public static final String FAREWELL = "Goodbye";

}
```

### Generated Class for Localized Properties

When processing property files that follow the `basename_language.properties` convention (e.g., `messages_en.properties`, `messages_de.properties` found in a directory specified in `<path>`), the plugin generates a Java class where each property key is represented as a `public static final java.util.Map<String, String>`.

*   The map's key is the language code string (e.g., "en", "de", "fr-CA").
*   The map's value is the translated string for that language.

**Example:**

Given property files:
*   `src/main/resources/i18n/apptexts_en.properties`:
    ```properties
    app.title=My Application
    button.save=Save
    ```
*   `src/main/resources/i18n/apptexts_de.properties`:
    ```properties
    app.title=Meine Anwendung
    button.save=Speichern
    ```

And the following configuration:
```xml
<source>
    <path>src/main/resources/i18n</path>
    <targetPackage>com.example.i18n</targetPackage>
</source>
```

The plugin (using `DefaultClassNameStrategy`) would generate `com.example.i18n.ApptextsProperties.java` (basename "apptexts" -> "ApptextsProperties"):

```java
package com.example.i18n;

import javax.annotation.Generated;
import java.util.Map;
import java.util.HashMap;

/**
 * Contains constants generated from the properties group: 'apptexts_*.properties'.
 * <p>
 * This class is automatically generated by the property-constant-maven-plugin.
 * Do not modify this file directly.
 */
@Generated(
    value = "io.github.yottabytecrafter.PropertiesGeneratorMojo",
    date = "YYYY-MM-DDTHH:mm:ssZ", // Example date
    comments = "Generated from apptexts_*.properties, version: 0.1, environment: Java X.Y.Z (Vendor)" // Example comment
)
public final class ApptextsProperties {

    private ApptextsProperties() {
        // Prevent instantiation
    }

    /**
     * Localized constant for property key: 'app.title'.
     * Contains translations for various languages.
     */
    public static final java.util.Map<String, String> APP_TITLE;

    /**
     * Localized constant for property key: 'button.save'.
     * Contains translations for various languages.
     */
    public static final java.util.Map<String, String> BUTTON_SAVE;

    static {
        APP_TITLE = new java.util.HashMap<>();
        APP_TITLE.put("en", "My Application");
        APP_TITLE.put("de", "Meine Anwendung");

        BUTTON_SAVE = new java.util.HashMap<>();
        BUTTON_SAVE.put("en", "Save");
        BUTTON_SAVE.put("de", "Speichern");
    }
}
```
*(Note: The `@Generated` annotation will contain the actual generation timestamp, plugin version, and Java environment details.)*

**Usage in your code:**

```java
// Assuming ApptextsProperties is imported
String titleInEnglish = ApptextsProperties.APP_TITLE.get("en");
String titleInGerman = ApptextsProperties.APP_TITLE.get("de");

// For a button label
String saveButtonEnglish = ApptextsProperties.BUTTON_SAVE.get("en");
```

This structure provides a type-safe way to access all translations of a given property key.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the plugin.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
