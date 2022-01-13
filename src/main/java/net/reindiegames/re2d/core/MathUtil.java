package net.reindiegames.re2d.core;

import org.joml.Matrix4f;

public final class MathUtil {
    private MathUtil() {
    }

    public static void toFloatArray(Matrix4f matrix, float[] dest) {
        dest[0] = matrix.m00();
        dest[1] = matrix.m01();
        dest[2] = matrix.m02();
        dest[3] = matrix.m03();

        dest[4] = matrix.m10();
        dest[5] = matrix.m11();
        dest[6] = matrix.m12();
        dest[7] = matrix.m13();

        dest[8] = matrix.m20();
        dest[9] = matrix.m21();
        dest[10] = matrix.m22();
        dest[11] = matrix.m23();

        dest[12] = matrix.m30();
        dest[13] = matrix.m31();
        dest[14] = matrix.m32();
        dest[15] = matrix.m33();
    }
}
