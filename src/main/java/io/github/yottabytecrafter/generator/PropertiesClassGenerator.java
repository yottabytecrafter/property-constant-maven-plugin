package io.github.yottabytecrafter.generator;

import io.github.yottabytecrafter.builder.ClassBuilder;
import io.github.yottabytecrafter.strategy.DefaultClassNameStrategy;
import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import io.github.yottabytecrafter.utils.ConstantNameConverter;
import io.github.yottabytecrafter.utils.StringEscapeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
            String value = StringEscapeUtils.escapeJavaString(properties.getProperty(key));
            builder.addConstant(constantName, value);
        }

        File outputFile = new File(packageDir, className + ".java");
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.print(builder.build());
        }
    }
}
