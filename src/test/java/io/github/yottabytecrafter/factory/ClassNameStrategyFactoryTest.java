package io.github.yottabytecrafter.factory;

import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import io.github.yottabytecrafter.strategy.DefaultClassNameStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassNameStrategyFactoryTest {
    @Test
    void shouldReturnDefaultStrategyForNullInput() {
        ClassNameStrategy strategy = ClassNameStrategyFactory.createStrategy(null);
        assertInstanceOf(DefaultClassNameStrategy.class, strategy);
    }

    @Test
    void shouldReturnDefaultStrategyForEmptyInput() {
        ClassNameStrategy strategy = ClassNameStrategyFactory.createStrategy("");
        assertInstanceOf(DefaultClassNameStrategy.class, strategy);
    }

    @Test
    void shouldReturnDefaultStrategyForBlankInput() {
        ClassNameStrategy strategy = ClassNameStrategyFactory.createStrategy("   ");
        assertInstanceOf(DefaultClassNameStrategy.class, strategy);
    }

    @Test
    void shouldCreateCustomStrategy() {
        ClassNameStrategy strategy = ClassNameStrategyFactory.createStrategy(CustomTestStrategy.class.getName());
        assertInstanceOf(CustomTestStrategy.class, strategy);
    }

    @Test
    void shouldThrowExceptionForNonExistentClass() {
        String nonExistentClass = "com.example.NonExistentStrategy";
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ClassNameStrategyFactory.createStrategy(nonExistentClass)
        );
        assertEquals(
                "Could not instantiate strategy class: " + nonExistentClass,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionForNonStrategyClass() {
        String nonStrategyClass = String.class.getName();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ClassNameStrategyFactory.createStrategy(nonStrategyClass)
        );
        assertEquals(
                "Class " + nonStrategyClass + " does not implement ClassNameStrategy",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionForClassWithoutDefaultConstructor() {
        String noDefaultConstructorClass = StrategyWithoutDefaultConstructor.class.getName();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ClassNameStrategyFactory.createStrategy(noDefaultConstructorClass)
        );
        assertTrue(exception.getMessage().startsWith("Could not instantiate strategy class:"));
    }

    public static class CustomTestStrategy implements ClassNameStrategy {
        @Override
        public String generateClassName(String propertyFile) {
            return "TestClass";
        }
    }

    public static class StrategyWithoutDefaultConstructor implements ClassNameStrategy {
        private final String param;

        public StrategyWithoutDefaultConstructor(String param) {
            this.param = param;
        }

        @Override
        public String generateClassName(String propertyFile) {
            return param;
        }
    }
}
