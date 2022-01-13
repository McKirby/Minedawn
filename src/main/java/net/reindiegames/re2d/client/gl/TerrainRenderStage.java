package net.reindiegames.re2d.client.gl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static net.reindiegames.re2d.client.gl.LevelRenderPipeline.CLEAR_COLOR;

public class TerrainRenderStage extends RenderStage<TerrainShader> {
    private final IntBuffer heightBuffer;
    private final IntBuffer widthBuffer;

    private SpriteMesh mesh;

    public TerrainRenderStage() {
        super(new TerrainShader());
        this.widthBuffer = MemoryUtil.memAllocInt(1);
        this.heightBuffer = MemoryUtil.memAllocInt(1);
        mesh = SpriteMesh.create("test", new float[] {
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
        });
    }

    @Override
    public void load() {
    }

    @Override
    public void prepare(Camera camera, long window) {
        GLFW.glfwGetWindowSize(window, widthBuffer, heightBuffer);
        int width = widthBuffer.get(0);
        int height = heightBuffer.get(0);
        GL11.glViewport(0, 0, width, height);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        GL11.glClearColor(CLEAR_COLOR.x, CLEAR_COLOR.y, CLEAR_COLOR.z, CLEAR_COLOR.w);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        shader.bind();
    }

    @Override
    public void process(long totalTicks, boolean debug) {
        GL30.glBindVertexArray(mesh.vao);
        if (debug) {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.lineIndicesVbo);
            GL11.glDrawElements(GL11.GL_LINES, mesh.lineIndices.length, GL11.GL_UNSIGNED_INT, 0);
        } else {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.triangleIndicesVbo);
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.triangleIndices.length, GL11.GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public void finish() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        Shader.unbind();
    }

    @Override
    public void yield() {
    }
}
