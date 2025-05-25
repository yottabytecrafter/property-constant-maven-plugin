package io.github.yottabytecrafter.utils;

public class ConstantNameConverter {

    public static String toConstantName(String propertyKey) {
        if (propertyKey == null || propertyKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Property key cannot be null or empty");
        }

        String originalKey = propertyKey; // Store original key for potential error messages

        String constantName = propertyKey.toUpperCase()
                .replace('.', '_')
                .replace('-', '_')
                .replace(' ', '_')
                .replaceAll("[^A-Z0-9_]", "");

        // If the generated constant name starts with a digit, prefix it with an underscore
        if (constantName.matches("^[0-9].*")) {
            constantName = "_" + constantName;
        }

        // If the generated constant name is empty OR consists only of underscores
        if (constantName.isEmpty() || constantName.matches("^_*$")) {
            throw new IllegalArgumentException(
                    String.format("Property key '%s' results in an invalid or empty constant name after conversion.", originalKey)
            );
        }

        return constantName;
    }
}