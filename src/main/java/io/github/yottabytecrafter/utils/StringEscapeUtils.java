package io.github.yottabytecrafter.utils;

public class StringEscapeUtils {

    public static String escapeJavaString(String value) {

        if (value == null) {
            return null;
        }

        StringBuilder escaped = new StringBuilder(value.length() * 2);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if (ch < 32 || ch > 127) {
                        escaped.append(String.format("\\u%04x", (int)ch));
                    } else {
                        escaped.append(ch);
                    }
            }
        }
        return escaped.toString();
    }
}
