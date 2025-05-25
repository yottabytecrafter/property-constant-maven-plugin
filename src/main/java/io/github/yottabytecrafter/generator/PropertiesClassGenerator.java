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
import java.util.Map; // Added
// Properties is no longer directly used in the new generateClass signature

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

    // Old generateClass method removed or commented out if necessary.
    // For this task, we replace it.

    public void generateClass(String baseName, Map<String, Map<String, String>> propertyTranslations) throws IOException {
        // Use baseName for class naming. DefaultClassNameStrategy will capitalize it.
        // If a suffix like ".properties" is needed by a specific strategy, 
        // the strategy itself should handle it or be configured.
        String className = classNameStrategy.generateClassName(baseName); 
        File packageDir = new File(outputDirectory, packageName.replace('.', '/'));
        if (!packageDir.exists()) {
            packageDir.mkdirs();
        }

        ClassBuilder builder = new ClassBuilder(className, packageName, pluginVersion)
                // Indicate that the class is generated from a group of property files
                .addGenerated(baseName + "_*.properties") 
                .makeClassFinal()
                .addPrivateConstructor();
        
        // Add imports for Map and HashMap, ClassBuilder will handle duplicates.
        // These are added by addLocalizedConstant now, but good to be aware.
        // builder.addImport("java.util.Map");
        // builder.addImport("java.util.HashMap");


        for (Map.Entry<String, Map<String, String>> entry : propertyTranslations.entrySet()) {
            String originalKey = entry.getKey();
            Map<String, String> translations = entry.getValue();
            String constantName = ConstantNameConverter.toConstantName(originalKey);
            
            // Call the new method in ClassBuilder to add a Map field and its static initialization
            builder.addLocalizedConstant(originalKey, constantName, translations);
        }

        File outputFile = new File(packageDir, className + ".java");
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            writer.print(builder.build());
        }
    }
}
