package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Transformable;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import static net.reindiegames.re2d.client.ClientParameters.*;
import static net.reindiegames.re2d.core.CoreParameters.debug;

public abstract class LevelRenderStage<S extends LevelShader, O> extends RenderStage<S, O> {
    protected LevelRenderStage(S shader) {
        super(shader);
    }

    @Override
    protected void load() {
    }

    @Override
    protected void prepare(long window, Vector2f c) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        shader.bind();
        shader.loadProjectionView(c, windowWidth, windowHeight);
    }

    @Override
    protected void finish() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        Shader.unbind();
    }

    @Override
    protected void yield() {
    }

    protected final void renderMesh(Transformable t, Mesh mesh) {
        GL30.glBindVertexArray(mesh.vao);

        if (t.changed || tileScaleChanged) {
            Shader.generateTransformation(t.transformation, t.getPosition(), t.size, t.getRotation());
        }

        shader.loadTransformation(t.transformation);
        t.changed = false;

        if (debug) {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.lineIndicesVbo);
            GL11.glDrawElements(GL11.GL_LINES, mesh.lineIndices.length, GL11.GL_UNSIGNED_INT, 0);
        } else {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.triangleIndicesVbo);
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.triangleIndices.length, GL11.GL_UNSIGNED_INT, 0);
        }
    }
}
