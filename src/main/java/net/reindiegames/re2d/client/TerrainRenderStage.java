package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.CoordinateSystems;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static net.reindiegames.re2d.client.ClientParameters.clearColor;
import static net.reindiegames.re2d.client.ClientParameters.tileScale;
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
        int width = widthBuffer.get(0);
        int height = heightBuffer.get(0);
        GL11.glViewport(0, 0, width, height);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        shader.bind();
        shader.loadProjectionView(ctx, cty, width, height);
        shader.loadTextureBank(0);
    }

    @Override
    protected void process(long totalTicks, Level level) {
        level.getChunkBase().forEachLoadedChunk((chunk -> {
            int tileId;
            short variant;
            for (byte rx = 0; rx < Chunk.CHUNK_SIZE; rx++) {
                for (byte ry = 0; ry < Chunk.CHUNK_SIZE; ry++) {
                    tileId = chunk.tiles[0][rx][ry];
                    variant = chunk.variants[0][rx][ry];

                    final SpriteMesh mesh = ClientCoreBridge.TILE_SPRITE_MAP.get(tileId).get(variant)[0];
                    ClientCoreBridge.TILE_ATLAS_MAP.get(tileId).bind(0);

                    GL30.glBindVertexArray(mesh.vao);

                    final Vector2f tileLevelPos = CoordinateSystems.chunkRelativeToLevel(chunk.cx, chunk.cy, rx, ry);
                    tileLevelPos.mul(tileScale);
                    shader.loadTransformation(tileLevelPos, 0.0f, new Vector2f(tileScale, tileScale));

                    if (debug) {
                        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.lineIndicesVbo);
                        GL11.glDrawElements(GL11.GL_LINES, mesh.lineIndices.length, GL11.GL_UNSIGNED_INT, 0);
                    } else {
                        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.triangleIndicesVbo);
                        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.triangleIndices.length, GL11.GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }));
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
}
