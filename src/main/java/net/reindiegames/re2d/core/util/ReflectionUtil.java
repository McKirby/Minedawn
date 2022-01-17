package net.reindiegames.re2d.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static void invokeAnnotatedStatics(Class<?> clazz, Class<? extends Annotation> annotation) throws Exception {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                method.setAccessible(true);
                method.invoke(null);
            }
        }
    }

    public static void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
