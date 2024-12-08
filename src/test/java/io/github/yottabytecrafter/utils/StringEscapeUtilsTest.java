package io.github.yottabytecrafter.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringEscapeUtilsTest {

    @Test
    void escapeJavaString() {
        // Grundlegende Escape-Sequenzen
        assertEquals(".", StringEscapeUtils.escapeJavaString("."));
        assertEquals("\\\"", StringEscapeUtils.escapeJavaString("\""));
        assertEquals("\\\\", StringEscapeUtils.escapeJavaString("\\"));
        assertEquals("\\n", StringEscapeUtils.escapeJavaString("\n"));
        assertEquals("\\r", StringEscapeUtils.escapeJavaString("\r"));
        assertEquals("\\t", StringEscapeUtils.escapeJavaString("\t"));

        // Null-Eingabe
        assertNull(StringEscapeUtils.escapeJavaString(null));

        // Leerer String
        assertEquals("", StringEscapeUtils.escapeJavaString(""));

        // Kombinierte Escape-Sequenzen
        assertEquals("Hello\\nWorld", StringEscapeUtils.escapeJavaString("Hello\nWorld"));
        assertEquals("Tab\\there", StringEscapeUtils.escapeJavaString("Tab\there"));
        assertEquals("\\\"Quote\\\"", StringEscapeUtils.escapeJavaString("\"Quote\""));

        // Unicode-Zeichen unter 32
        assertEquals("\\u0000", StringEscapeUtils.escapeJavaString("\u0000"));
        assertEquals("\\u001f", StringEscapeUtils.escapeJavaString("\u001F"));

        // Zeichen über 127 (extended ASCII und Unicode)
        assertEquals("\\u0080", StringEscapeUtils.escapeJavaString("\u0080")); // Erstes Zeichen über 127
        assertEquals("\\u00a9", StringEscapeUtils.escapeJavaString("\u00A9")); // Copyright-Symbol ©
        assertEquals("\\u00e4", StringEscapeUtils.escapeJavaString("\u00E4")); // ä
        assertEquals("\\u00f6", StringEscapeUtils.escapeJavaString("\u00F6")); // ö
        assertEquals("\\u20ac", StringEscapeUtils.escapeJavaString("\u20AC")); // Euro-Symbol €
        assertEquals("Hello\\u0080World", StringEscapeUtils.escapeJavaString("Hello\u0080World"));

        // Normale ASCII-Zeichen
        assertEquals("Hello World!", StringEscapeUtils.escapeJavaString("Hello World!"));
        assertEquals("123ABC", StringEscapeUtils.escapeJavaString("123ABC"));

        // Komplexe Kombinationen
        assertEquals("Path: C:\\\\temp\\\\new\\nLine", StringEscapeUtils.escapeJavaString("Path: C:\\temp\\new\nLine"));
        assertEquals("\\t\\\"Hello\\\"\\n\\\\World\\\\", StringEscapeUtils.escapeJavaString("\t\"Hello\"\n\\World\\"));
        assertEquals("Copyright: \\u00a9 2024", StringEscapeUtils.escapeJavaString("Copyright: \u00A9 2024"));
    }
}