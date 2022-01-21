package net.reindiegames.re2d.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

class Mesh {
    protected static final float[] SPRITE_VERTICES = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };
    protected static final int[] SPRITE_TRIANGLE_INDICES = {
            0, 2, 1,
            1, 2, 3,
    };
    protected static final int[] SPRITE_LINE_INDICES = {
            0, 2,
            2, 3,
            3, 1,
            1, 0,
    };

    private static final Set<Integer> createdVaos = new HashSet<>();
    private static final Set<Integer> createdVbos = new HashSet<>();
    protected final int vao;
    protected final int vertexVbo;
    protected final int textureCoordinateVbo;
    protected final int triangleIndicesVbo;
    protected final int lineIndicesVbo;

    protected final String name;
    protected final float[] vertices;
    protected final float[] textureCoordinates;
    protected final int[] triangleIndices;
    protected final int[] lineIndices;

    protected Mesh(String name, float[] v, float[] t, int[] triangleIndices, int[] lineIndices) {
        this.name = name;
        this.vertices = v;
        this.textureCoordinates = t;
        this.triangleIndices = triangleIndices;
        this.lineIndices = lineIndices;

        this.vao = GL30.glGenVertexArrays();
        createdVaos.add(vao);

        GL30.glBindVertexArray(vao);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        this.vertexVbo = this.storeAsAttribute(0, v, 2);
        this.textureCoordinateVbo = this.storeAsAttribute(1, t, 2);
        this.triangleIndicesVbo = this.storeAsIndices(triangleIndices);
        this.lineIndicesVbo = this.storeAsIndices(lineIndices);

        GL30.glBindVertexArray(0);
    }

    protected static Mesh create(String name, float[] textureCoordinates) {
        return new Mesh(name, SPRITE_VERTICES, textureCoordinates, SPRITE_TRIANGLE_INDICES, SPRITE_LINE_INDICES);
    }

    protected static void dispose() {
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        for (int vbo : createdVbos) {
            GL30.glDeleteBuffers(vbo);
        }
        createdVbos.clear();

        GL30.glBindVertexArray(0);
        for (int vao : createdVaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        createdVaos.clear();
    }

    private int storeAsAttribute(int index, float[] data, int dimension) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data);
        buffer.flip();

        int vbo = GL30.glGenBuffers();
        createdVbos.add(vbo);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(index, dimension, GL11.GL_FLOAT, false, 0, 0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);

        return vbo;
    }

    private int storeAsIndices(int[] indices) {
        IntBuffer buffer = MemoryUtil.memAllocInt(indices.length);
        buffer.put(indices);
        buffer.flip();

        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        return vbo;
    }

    protected void delete() {
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(vertexVbo);
        GL30.glDeleteBuffers(textureCoordinateVbo);
        GL30.glDeleteBuffers(triangleIndicesVbo);
        GL30.glDeleteBuffers(lineIndicesVbo);
        createdVbos.remove(vertexVbo);
        createdVbos.remove(textureCoordinateVbo);
        createdVbos.remove(triangleIndicesVbo);
        createdVbos.remove(lineIndicesVbo);

        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vao);
        createdVaos.remove(vao);
    }
}
