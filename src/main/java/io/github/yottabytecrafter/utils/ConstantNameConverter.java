package io.github.yottabytecrafter.utils;

public class ConstantNameConverter {

    public static String toConstantName(String propertyKey) {
        if (propertyKey == null || propertyKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Property key cannot be null or empty");
        }

        return propertyKey.toUpperCase()
                .replace('.', '_')
                .replace('-', '_')
                .replace(' ', '_')
                .replaceAll("[^A-Z0-9_]", "");
    }
}