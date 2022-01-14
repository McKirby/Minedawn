package net.reindiegames.re2d.core.util;

public final class MathUtil {
    private MathUtil() {
    }

    public static final double sum(double[] array) {
        double sum = 0.0D;

        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }

        return sum;
    }

    public static final void multiply(double[] array, double factor) {
        for (int i = 0; i < array.length; i++) {
            array[i] *= factor;
        }
    }

    public static final double min(double[] array) {
        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }

        return min;
    }
}
