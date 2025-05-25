package com.example.it;

import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import java.io.File;

public class CustomStrategy implements ClassNameStrategy {
    @Override
    public String generateClassName(String propertiesFileName) {
        // Simple strategy: prepend "Custom_" and remove ".properties"
        String baseName = propertiesFileName.endsWith(".properties") ?
                                propertiesFileName.substring(0, propertiesFileName.length() - ".properties".length()) :
                                propertiesFileName;
        return "Custom_" + baseName.replaceAll("[^a-zA-Z0-9_]", ""); // Basic sanitization
    }
}
