package io.github.yottabytecrafter.strategy;

public interface ClassNameStrategy {
    /**
     * Generiert einen Klassennamen aus einem Dateinamen.
     *
     * @param filename Der Name der Properties-Datei
     * @return Der generierte Klassenname
     * @throws IllegalArgumentException wenn filename null oder leer ist
     */
    String generateClassName(String filename);
}
