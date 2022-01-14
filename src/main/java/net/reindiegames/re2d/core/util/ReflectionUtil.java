package net.reindiegames.re2d.core.util;

import java.lang.annotation.Annotation;
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
}
