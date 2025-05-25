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
*   **Generated Javadoc:** Automatically generates class-level Javadoc (including the source properties file name) and constant-level Javadoc (including the original property key) for better usability and documentation.
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
                        <!-- Path to *.properties File -->
                        <path>${project.basedir}/src/main/resources/message.properties</path>
                        <targetPackage>io.github.yottabytecrafter.config</targetPackage>
                    </source>
                    <source>
                        <!-- Path to directory with *.properties Files -->
                        <path>${project.basedir}/src/main/resources</path>
                        <targetPackage>io.github.yottabytecrafter.config</targetPackage>
                    </source>
                </sources>
                <!-- Optional output directory -->
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
| `sources`                | A list of source configurations, each defining a path and optionally a target package.                           |                                                                |
| `source`                 | Specifies the path to the directory or property file.                                                             |                                                                |
| `path`                   | The path to the directory or property file.                                                                      |                                                                |
| `targetPackage`          | The default target package for generated classes if not specified in `source`.                                   |                                                                |
| `outputDirectory`        | The output directory for the generated Java classes.                                                              | `${project.build.directory}/generated-sources/java`            |
| `propertiesEncoding`     | The character encoding to use when reading the .properties files.                                                 | `UTF-8`                                                        |
| `classNameStrategyClass` | Optional: Fully qualified class name of a custom `ClassNameStrategy` implementation. This allows you to define your own logic for how Java class names are generated from property file names.                             | `io.github.yottabytecrafter.strategy.DefaultClassNameStrategy` |


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
    public String generateClassName(String propertiesFileName) {
        // Example: Remove ".properties" and prepend "Config_"
        String baseName = propertiesFileName.endsWith(".properties") ?
                          propertiesFileName.substring(0, propertiesFileName.length() - ".properties".length()) :
                          propertiesFileName;
        // Basic sanitization to ensure valid Java class name characters
        String sanitizedBaseName = baseName.replaceAll("[^a-zA-Z0-9_]", "");
        if (sanitizedBaseName.isEmpty()) {
            // Fallback for names that become empty after sanitization
            return "Config_Default"; 
        }
        // Ensure it starts with a letter or underscore if it's not already
        if (Character.isDigit(sanitizedBaseName.charAt(0))) {
            sanitizedBaseName = "_" + sanitizedBaseName;
        }
        return "Config_" + sanitizedBaseName;
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
public final class MessageProperties {

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
*(Note: The `@Generated` annotation will contain the actual generation timestamp, plugin version, and Java environment details.)*

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the plugin.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
