package net.reindiegames.re2d.client.gl;

import net.reindiegames.re2d.core.MathUtil;
import net.reindiegames.util.Log;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

public abstract class Shader {
    protected static final Vector2f X_AXIS = new Vector2f(1.0f, 0.0f);
    protected static final Vector2f Y_AXIS = new Vector2f(0.0f, 1.0f);

    private static final Set<Integer> createdShaders = new HashSet<>();
    private static final Set<Integer> createdPrograms = new HashSet<>();

    public final String vertexFile;
    public final String fragmentFile;

    private final FloatBuffer matrixBuffer;

    protected int program = 0;
    protected int vertexShader = 0;
    protected int fragmentShader = 0;

    public Shader(String vertexFile, String fragmentFile) throws IllegalArgumentException {
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

    public static void dispose() {
        Shader.unbind();

        for (int shader : createdShaders) {
            GL30.glDeleteShader(shader);
        }

        for (int program : createdPrograms) {
            GL30.glDeleteProgram(program);
        }
    }

    private static int loadShader(String res, int type) throws IllegalArgumentException {
        String sourceCode = null;
        try {
            final InputStream in = Shader.class.getClassLoader().getResourceAsStream(res);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            final StringBuilder buffer = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            reader.close();
            sourceCode = buffer.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read the Resource '" + res + "'!");
        }

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

    public static void unbind() {
        GL30.glUseProgram(0);
    }

    public abstract void loadUniforms();

    public void bind() {
        GL30.glUseProgram(program);
    }

    public final int getUniformLocation(String variable) {
        return GL30.glGetUniformLocation(program, variable);
    }

    public void loadFloat(int loc, float value) {
        GL30.glUniform1f(loc, value);
    }

    public void loadInteger(int loc, int value) {
        GL30.glUniform1i(loc, value);
    }

    public void loadBoolean(int loc, boolean value) {
        GL30.glUniform1i(loc, value ? 1 : 0);
    }

    public void loadVector3f(int loc, Vector3f vec) {
        GL30.glUniform3f(loc, vec.x, vec.y, vec.z);
    }

    public void loadVector2f(int loc, Vector2f vec) {
        GL30.glUniform2f(loc, vec.x, vec.y);
    }

    public void loadVector4f(int loc, Vector4f vec) {
        GL30.glUniform4f(loc, vec.x, vec.y, vec.z, vec.w);
    }

    public void loadMatrix4f(int loc, Matrix4f value) {
        float[] array = new float[16];
        MathUtil.toFloatArray(value, array);

        GL30.glUniformMatrix4fv(loc, false, array);
    }

    public void loadMatrixArray(int loc, float[] matrix) {
        GL30.glUniformMatrix4fv(loc, false, matrix);
    }
}
