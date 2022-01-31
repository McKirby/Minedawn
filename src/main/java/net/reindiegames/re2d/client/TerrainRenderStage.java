package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.Transformable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.reindiegames.re2d.client.ClientCoreBridge.CHUNK_MESH_MAP;
import static net.reindiegames.re2d.client.ClientParameters.*;
import static net.reindiegames.re2d.core.CoreParameters.debug;

class TerrainRenderStage extends RenderStage<TerrainShader, Level> {
    private final IntBuffer heightBuffer;
    private final IntBuffer widthBuffer;

    protected TerrainRenderStage() {
        super(new TerrainShader());
        this.widthBuffer = MemoryUtil.memAllocInt(1);
        this.heightBuffer = MemoryUtil.memAllocInt(1);
    }

    @Override
    protected void load() {
    }

    protected void prepare(long window, float ctx, float cty) {
        GLFW.glfwGetWindowSize(window, widthBuffer, heightBuffer);
        windowWidth = widthBuffer.get(0);
        windowHeight = heightBuffer.get(0);
        GL11.glViewport(0, 0, windowWidth, windowHeight);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        shader.bind();
        shader.loadProjectionView(ctx, cty, windowWidth, windowHeight);
        shader.loadTextureBank(0);
    }

    @Override
    protected void process(long totalTicks, Level level) {
        shader.loadDepth(0.0f);
        level.getChunkBase().forEachLoadedChunk((chunk -> {
            Map<Integer, Mesh[]> xMap = CHUNK_MESH_MAP.computeIfAbsent(chunk.cx, key -> new HashMap<>());
            Mesh[] meshes = xMap.getOrDefault(chunk.cy, null);
            if (chunk.changed || meshes == null) {
                if (meshes != null) {
                    for (int i = 0; i < meshes.length; i++) {
                        meshes[i].delete();
                    }
                }
                meshes = ClientCoreBridge.generateTerrainMesh(chunk);
                xMap.put(chunk.cy, meshes);
            }
            this.renderMesh(chunk, meshes[(int) (totalTicks % meshes.length)]);
        }));

        shader.loadDepth(1.0f);
        level.getChunkBase().forEachEntity(entity -> {
            RenderCompound compound = ClientCoreBridge.ENTITY_COMPOUND_MAP.get(entity.type.id);
            RenderCompound.AnimationParameters p = compound.animation[entity.state];

            this.renderMesh(entity,
                    compound.sprites[entity.state][p.frames == 1 ? 0 : (int) ((totalTicks / p.ticks) % p.frames)]
            );
        });
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

    private final void renderMesh(Transformable t, Mesh mesh) {
        GL30.glBindVertexArray(mesh.vao);

        if (t.changed || tileScaleChanged) {
            Shader.generateTransformation(t.transformation, t.pos, t.size, t.rotation);
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
