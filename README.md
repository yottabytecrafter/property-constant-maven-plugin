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



## Example

Given a property file `src/main/resources/message.properties` with the following content:

```properties
greeting=Hello, World!
farewell=Goodbye
```

The plugin will generate a Java class `io.github.yottabytecrafter.config.MessageProperties` (if `io.github.yottabytecrafter.config` is the `targetPackage`):

```java
package io.github.yottabytecrafter.config;

public final class MessageProperties {
    public static final String GREETING = "Hello, World!";
    public static final String FAREWELL = "Goodbye";

    private MessageProperties() {
        throw new IllegalStateException("Utility class");
    }
}
```

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the plugin.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
