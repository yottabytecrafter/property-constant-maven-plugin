package io.github.yottabytecrafter.factory;

import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import io.github.yottabytecrafter.strategy.DefaultClassNameStrategy;

public class ClassNameStrategyFactory {
    public static ClassNameStrategy createStrategy(String strategyClass) {
        if (strategyClass == null || strategyClass.trim().isEmpty()) {
            return new DefaultClassNameStrategy();
        }

        try {
            Class<?> clazz = Class.forName(strategyClass);
            if (!ClassNameStrategy.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Class " + strategyClass + " does not implement ClassNameStrategy");
            }
            return (ClassNameStrategy) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Could not instantiate strategy class: " + strategyClass, e);
        }
    }
}
