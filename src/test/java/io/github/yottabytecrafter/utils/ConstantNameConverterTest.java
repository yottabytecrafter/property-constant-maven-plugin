package io.github.yottabytecrafter.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ConstantNameConverterTest {

    @Test
    void toConstantName() {
        // Grundlegende Umwandlungen
        assertEquals("PROPERTY_NAME", ConstantNameConverter.toConstantName("property.name"));
        assertEquals("PROPERTY_NAME", ConstantNameConverter.toConstantName("property-name"));
        assertEquals("PROPERTY_NAME", ConstantNameConverter.toConstantName("property name"));

        // Bereits großgeschriebene Strings
        assertEquals("PROPERTY_NAME", ConstantNameConverter.toConstantName("PROPERTY_NAME"));
        assertEquals("PROPERTY_NAME", ConstantNameConverter.toConstantName("PROPERTY.NAME"));

        // Zahlen
        assertEquals("PROPERTY123", ConstantNameConverter.toConstantName("property123"));
        assertEquals("_123PROPERTY", ConstantNameConverter.toConstantName("123property")); // Updated assertion
        assertEquals("PROPERTY_123_NAME", ConstantNameConverter.toConstantName("property.123.name"));

        // Sonderzeichen, die entfernt werden sollen
        assertEquals("PROPERTYNAME", ConstantNameConverter.toConstantName("property!name"));
        assertEquals("PROPERTYNAME", ConstantNameConverter.toConstantName("property@name"));
        assertEquals("PROPERTYNAME", ConstantNameConverter.toConstantName("property#name"));
        assertEquals("PROPERTYNAME", ConstantNameConverter.toConstantName("property$name"));
        assertEquals("PROPERTYNAME", ConstantNameConverter.toConstantName("property%name"));

        // Mehrere aufeinanderfolgende Sonderzeichen
        assertEquals("PROPERTY___NAME", ConstantNameConverter.toConstantName("property...name"));
        assertEquals("PROPERTY___NAME", ConstantNameConverter.toConstantName("property---name"));
        assertEquals("PROPERTY___NAME", ConstantNameConverter.toConstantName("property   name"));

        // Kombinierte Fälle
        assertEquals("MY_PROPERTY_NAME_123", ConstantNameConverter.toConstantName("my.property-name 123"));
        assertEquals("MY_PROPERTY_NAME_123", ConstantNameConverter.toConstantName("MY.property-NAME 123"));
        assertEquals("MYPROPERTYNAME_123", ConstantNameConverter.toConstantName("my!@#property$%^name 123"));

        // Edge Cases
        assertEquals("A", ConstantNameConverter.toConstantName("a"));
        // assertEquals("_", ConstantNameConverter.toConstantName("_")); // This will now throw an exception
        // assertEquals("___", ConstantNameConverter.toConstantName("___")); // This will now throw an exception
    }

    @Test
    void testNewConversionRules() {
        // Test case for a property key that results in a constant name starting with a digit
        assertEquals("_123_KEY", ConstantNameConverter.toConstantName("123.key"));
        assertEquals("_1VALUE", ConstantNameConverter.toConstantName("1value"));
        assertEquals("_1_VALUE", ConstantNameConverter.toConstantName("1 value"));
        assertEquals("_1_VALUE", ConstantNameConverter.toConstantName("1-value"));

        // Test case for a valid key that also contains numbers not at the beginning
        assertEquals("KEY123_NAME", ConstantNameConverter.toConstantName("key123.name"));
        assertEquals("MY_PROPERTY_NAME_123", ConstantNameConverter.toConstantName("my.property-name 123"));

        // Test for a key that would result in multiple underscores
        assertEquals("KEY__NAME", ConstantNameConverter.toConstantName("key..name"));
        assertEquals("KEY___NAME", ConstantNameConverter.toConstantName("key...name"));
    }

    @Test
    void testInvalidInputsAfterConversion() {
        // Test case for a property key that results in an empty name after sanitization
        Exception ex1 = assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("!@#$"));
        assertEquals("Property key '!@#$' results in an invalid or empty constant name after conversion.", ex1.getMessage());

        // Test case for a property key that consists only of characters that are converted to underscores
        Exception ex2 = assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(".-.-"));
        assertEquals("Property key '.-.-' results in an invalid or empty constant name after conversion.", ex2.getMessage());

        // Removed test for " " as it's handled by the initial trim().isEmpty() check and throws "Property key cannot be null or empty"
        // Exception ex3 = assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(" "));
        // assertEquals("Property key ' ' results in an invalid or empty constant name after conversion.", ex3.getMessage());

        Exception ex4 = assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("___"));
        assertEquals("Property key '___' results in an invalid or empty constant name after conversion.", ex4.getMessage());

        Exception ex5 = assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("_"));
        assertEquals("Property key '_' results in an invalid or empty constant name after conversion.", ex5.getMessage());
        
        Exception ex6 = assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("!@#$%^&*()"));
        assertEquals("Property key '!@#$%^&*()' results in an invalid or empty constant name after conversion.", ex6.getMessage());
    }

    @Test
    void invalidInput() {
        // These test the initial null/empty check
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(null), "Property key cannot be null or empty");
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(""), "Property key cannot be null or empty");
        Exception exSpace = assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(" ")); // Re-enabled and assert specific message
        assertEquals("Property key cannot be null or empty", exSpace.getMessage());
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("\t"), "Property key cannot be null or empty");
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("   "), "Property key cannot be null or empty");
    }
}