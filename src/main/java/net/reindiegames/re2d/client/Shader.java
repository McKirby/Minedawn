package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.io.IO;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

abstract class Shader {
    private static final Set<Integer> createdShaders = new HashSet<>();
    private static final Set<Integer> createdPrograms = new HashSet<>();

    protected final String vertexFile;
    protected final String fragmentFile;

    private final FloatBuffer matrixBuffer;

    protected int program = 0;
    protected int vertexShader = 0;
    protected int fragmentShader = 0;

    protected Shader(String vertexFile, String fragmentFile) throws IllegalArgumentException {
        this.vertexFile = vertexFile;
        this.fragmentFile = fragmentFile;

        this.matrixBuffer = MemoryUtil.memAllocFloat(16);

        this.program = GL30.glCreateProgram();
        if (program == 0) throw new IllegalArgumentException("Can not create Shader-Program?!");
        createdPrograms.add(program);
        this.bind();

        this.vertexShader = Shader.loadShader(vertexFile, GL30.GL_VERTEX_SHADER);
        this.fragmentShader = Shader.loadShader(fragmentFile, GL30.GL_FRAGMENT_SHADER);

        GL30.glAttachShader(program, vertexShader);
        GL30.glAttachShader(program, fragmentShader);
        GL30.glLinkProgram(program);
        this.loadUniforms();
        GL30.glDetachShader(program, fragmentShader);
        GL30.glDetachShader(program, vertexShader);

        if (GL30.glGetProgrami(program, GL30.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new IllegalArgumentException("Can not Link Program!");
        }

        GL30.glValidateProgram(program);
        if (GL30.glGetProgrami(program, GL30.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            throw new IllegalArgumentException("Can not validate Program: " + GL30.glGetProgramInfoLog(program));
        }
    }

    protected static void dispose() {
        Shader.unbind();

        for (int shader : createdShaders) {
            GL30.glDeleteShader(shader);
        }

        for (int program : createdPrograms) {
            GL30.glDeleteProgram(program);
        }
    }

    protected static int loadShader(String res, int type) throws IllegalArgumentException {
        String sourceCode = IO.readResourceContent(res);

        final int shader = GL30.glCreateShader(type);
        if (shader == 0) throw new IllegalArgumentException("Can not create Shader?!");
        createdShaders.add(shader);

        GL30.glShaderSource(shader, sourceCode);
        GL30.glCompileShader(shader);

        if (GL30.glGetShaderi(shader, GL30.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Log.error(GL30.glGetShaderInfoLog(shader));
            throw new IllegalArgumentException("Can not compile Shader '" + res + "'!");
        }

        return shader;
    }

    protected static void unbind() {
        GL30.glUseProgram(0);
    }

    protected static void toFloatArray(Matrix4f matrix, float[] dest) {
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

    protected abstract void loadUniforms();

    protected void bind() {
        GL30.glUseProgram(program);
    }

    protected final int getUniformLocation(String variable) {
        return GL30.glGetUniformLocation(program, variable);
    }

    protected final void loadFloat(int loc, float value) {
        GL30.glUniform1f(loc, value);
    }

    protected final void loadInteger(int loc, int value) {
        GL30.glUniform1i(loc, value);
    }

    protected final void loadBoolean(int loc, boolean value) {
        GL30.glUniform1i(loc, value ? 1 : 0);
    }

    protected final void loadVector3f(int loc, Vector3f vec) {
        GL30.glUniform3f(loc, vec.x, vec.y, vec.z);
    }

    protected final void loadVector2f(int loc, Vector2f vec) {
        GL30.glUniform2f(loc, vec.x, vec.y);
    }

    protected final void loadVector4f(int loc, Vector4f vec) {
        GL30.glUniform4f(loc, vec.x, vec.y, vec.z, vec.w);
    }

    protected final void loadMatrix4f(int loc, Matrix4f value) {
        float[] array = new float[16];
        Shader.toFloatArray(value, array);

        GL30.glUniformMatrix4fv(loc, false, array);
    }

    protected final void loadMatrixArray(int loc, float[] matrix) {
        GL30.glUniformMatrix4fv(loc, false, matrix);
    }
}
