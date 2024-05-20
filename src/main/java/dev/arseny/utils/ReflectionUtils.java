package dev.arseny.utils;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {


    public static Object createClassForName(String fullyQualifiedName) {
        Object toReturn = null;
        try {
            // Load the class
            Class<?> clazz = Class.forName(fullyQualifiedName);

            // Create a new instance of the class
            toReturn = clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return toReturn;
    }
}
