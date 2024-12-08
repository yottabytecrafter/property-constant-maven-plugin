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
        assertEquals("123PROPERTY", ConstantNameConverter.toConstantName("123property"));
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
        assertEquals("_", ConstantNameConverter.toConstantName("_"));
        assertEquals("___", ConstantNameConverter.toConstantName("___"));
    }

    @Test
    void invalidInput() {
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(null));
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(""));
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName(" "));
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("\t"));
        assertThrows(IllegalArgumentException.class, () -> ConstantNameConverter.toConstantName("   "));
    }
}