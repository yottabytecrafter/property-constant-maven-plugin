# Property Constant Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.yottabytecrafter/property-constant-maven-plugin.svg)](https://search.maven.org/artifact/io.github.yottabytecrafter/property-constant-maven-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Description

The Message Maven Plugin is a tool designed to enhance Java development by automatically generating Java constant classes from property files. It simplifies the management of properties by creating corresponding Java classes containing static string constants for each key in the property files. This reduces the risk of runtime errors due to typos in property keys and improves code maintainability.

The plugin scans specified directories for `.properties` files during the build process and generates corresponding Java classes. Each property key is converted into a well-defined constant within the generated class, ensuring type safety and compile-time error checking. It also supports custom naming strategies for generated class names through the `ClassNameStrategy` interface.

## Features

*   **Automatic Java Class Generation:** Automatically generates Java classes from `.properties` files.
*   **Compile-Time Safety:** Ensures that property keys are valid identifiers at compile time.
*   **Customizable Class Naming:** Supports customizable class naming strategies via the `ClassNameStrategy` interface.
*   **Integration Tests:** Includes comprehensive integration tests to ensure reliability.
*   **Maven Integration:** Seamlessly integrates with the Maven build lifecycle.

## Usage

To use the Message Maven Plugin, add it to your project's `pom.xml` in the `<build>` section:

```xml

<build>
    <plugins>
        <plugin>
            <groupId>io.github.yottabytecrafter</groupId>
            <artifactId>property-constant-maven-plugin</artifactId>
            <version>0.1-SNAPSHOT</version>
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
                <!-- Optional class name strategy -->
                <classNameStrategyClass>io.github.yottabytecrafter.strategy.DefaultClassNameStrategy
                </classNameStrategyClass>
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
| `classNameStrategyClass` | The fully qualified class name of the `ClassNameStrategy` implementation to use for generating class names.     | `io.github.yottabytecrafter.strategy.DefaultClassNameStrategy` |

## Class Naming Strategy

The plugin allows you to customize the naming of generated Java classes by implementing the `ClassNameStrategy` interface.

**Default Strategy:**

The default strategy (`io.github.yottabytecrafter.strategy.DefaultClassNameStrategy`) creates class names based on the property file name, capitalizing the first letter and removing the `.properties` extension. The specific steps are as follows:

1. Remove the `.properties` extension from the file name.
2. Convert to camel case.
3. Return `"[ClassName]Properties"`

**Custom Strategy Example:**

You can create your own strategy by implementing the `ClassNameStrategy` interface. Here's an example:

```java
package io.github.yottabytecrafter.strategy;

import strategy.io.github.yottabytecrafter.ClassNameStrategy;

import java.io.File;

public class CustomClassNameStrategy implements ClassNameStrategy {

    @Override
    public String getClassName(File propertyFile) {
        String fileName = propertyFile.getName();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.')); // Remove .properties extension

        // Add a "Config" suffix to the class name
        return convertToClassName(baseName) + "Config";
    }

    private String convertToClassName(String name) {
        // Simple conversion: capitalize the first letter
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
```

To use this custom strategy, specify its fully qualified class name in the plugin configuration:

```xml
<configuration>
    <classNameStrategyClass>io.github.yottabytecrafter.strategy.CustomClassNameStrategy</classNameStrategyClass>
    <!-- ... other configurations ... -->
</configuration>
```

## Example

Given a property file `src/main/resources/message.properties` with the following content:

```properties
greeting=Hello, World!
farewell=Goodbye
```

The plugin will generate a Java class `io.github.yottabytecrafter.config.MessageConstants` (if `io.github.yottabytecrafter.config` is the `targetPackage`):

```java
package io.github.yottabytecrafter.config;

public final class MessageConstants {
    public static final String GREETING = "Hello, World!";
    public static final String FAREWELL = "Goodbye";

    private MessageConstants() {
        throw new IllegalStateException("Utility class");
    }
}
```

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues to improve the plugin.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
