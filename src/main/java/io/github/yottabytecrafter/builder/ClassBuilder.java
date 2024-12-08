package io.github.yottabytecrafter.builder;

import io.github.yottabytecrafter.utils.DateTimeUtils;

public class ClassBuilder {

    private final StringBuilder code = new StringBuilder();
    private final String className;
    private final String packageName;
    private String pluginVersion;

    public ClassBuilder(String className, String packageName, String pluginVersion) {
        this.className = className;
        this.packageName = packageName;
        this.pluginVersion = pluginVersion;
    }

    public ClassBuilder addGenerated(String sourceFile) {
        code.append("@Generated(\n")
                .append("    value = \"io.github.yottabytecrafter.PropertiesGeneratorMojo\",\n")
                .append("    date = \"").append(DateTimeUtils.getCurrentDateTime()).append("\",\n");
        addVersionToComments(sourceFile);
        code.append(")\n");
        return this;
    }

    private void addVersionToComments(String sourceFile) {
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");

        if (pluginVersion == null || pluginVersion.isEmpty()) {
            pluginVersion = "unknown";
        }

        String comments = String.format("Generated from %s, version: %s, environment: Java %s (%s)",
                sourceFile, pluginVersion, javaVersion, javaVendor);
        code.append("    comments = \"").append(comments).append("\"\n");
    }

    public ClassBuilder makeClassFinal() {
        code.append("public final class ").append(className).append(" {\n\n");
        return this;
    }

    public ClassBuilder addPrivateConstructor() {
        code.append("    private ").append(className).append("() {\n")
                .append("        // Prevent instantiation\n")
                .append("    }\n\n");
        return this;
    }

    public ClassBuilder addConstant(String name, String value) {
        code.append("    public static final String ")
                .append(name)
                .append(" = \"")
                .append(value)
                .append("\";\n");
        return this;
    }

    public String build() {
        StringBuilder result = new StringBuilder()
                .append("package ").append(packageName).append(";\n\n")
                .append("import javax.annotation.Generated;\n\n")
                .append(code)
                .append("}\n");
        return result.toString();
    }
}
