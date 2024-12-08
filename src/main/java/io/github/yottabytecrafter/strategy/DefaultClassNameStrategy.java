package io.github.yottabytecrafter.strategy;

public class DefaultClassNameStrategy implements ClassNameStrategy {

    @Override
    public String generateClassName(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        String baseName = filename;
        if (filename.endsWith(".properties")) {
            baseName = filename.substring(0, filename.lastIndexOf('.'));
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (int i = 0; i < baseName.length(); i++) {
            char c = baseName.charAt(i);
            if (c == '.') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result + "Properties";
    }
}
