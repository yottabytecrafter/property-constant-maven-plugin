package io.github.yottabytecrafter.generator;

import io.github.yottabytecrafter.builder.ClassBuilder;
import io.github.yottabytecrafter.strategy.DefaultClassNameStrategy;
import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import io.github.yottabytecrafter.utils.ConstantNameConverter;
import io.github.yottabytecrafter.utils.StringEscapeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesClassGenerator {

    private final String packageName;
    private final File outputDirectory;
    private final ClassNameStrategy classNameStrategy;
    private final String pluginVersion;


    public PropertiesClassGenerator(
            String packageName,
            File outputDirectory,
            ClassNameStrategy classNameStrategy,
            String pluginVersion) {
        this.packageName = packageName;
        this.outputDirectory = outputDirectory;
        this.classNameStrategy = classNameStrategy != null ?
                classNameStrategy : new DefaultClassNameStrategy();
        this.pluginVersion = pluginVersion;
    }

    public void generateClass(File propertiesFile, Properties properties) throws IOException {
        String className = classNameStrategy.generateClassName(propertiesFile.getName());
        File packageDir = new File(outputDirectory, packageName.replace('.', '/'));
        packageDir.mkdirs();

        ClassBuilder builder = new ClassBuilder(className, packageName, pluginVersion)
                .addGenerated(propertiesFile.getName())
                .makeClassFinal()
                .addPrivateConstructor();

        for (String key : properties.stringPropertyNames()) {
            String constantName = ConstantNameConverter.toConstantName(key);
            String escapedValue = StringEscapeUtils.escapeJavaString(properties.getProperty(key));
            // Pass the original key, the generated constant name, and the escaped value
            builder.addConstant(key, constantName, escapedValue);
        }

        File outputFile = new File(packageDir, className + ".java");
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            writer.print(builder.build());
        }
    }
}
