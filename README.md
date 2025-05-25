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
| `classNameStrategyClass` | Optional: Fully qualified class name of a custom `ClassNameStrategy` implementation.                             | `io.github.yottabytecrafter.strategy.DefaultClassNameStrategy` |


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
