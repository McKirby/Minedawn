package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static net.reindiegames.re2d.client.ClientParameters.*;

class LevelRenderPipeline {
    private final IntBuffer heightBuffer;
    private final IntBuffer widthBuffer;

    private final TerrainRenderStage terrainRenderStage;
    private final EntityRenderStage entityRenderStage;

    protected LevelRenderPipeline() {
        this.widthBuffer = MemoryUtil.memAllocInt(1);
        this.heightBuffer = MemoryUtil.memAllocInt(1);

        this.terrainRenderStage = new TerrainRenderStage();
        this.entityRenderStage = new EntityRenderStage();
    }

    protected void render(Level level, long window, Vector2f camera) {
        GLFW.glfwGetWindowSize(window, widthBuffer, heightBuffer);
        windowWidth = widthBuffer.get(0);
        windowHeight = heightBuffer.get(0);
        GL11.glViewport(0, 0, windowWidth, windowHeight);

        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        terrainRenderStage.render(level, window, camera);
        entityRenderStage.render(level, window, camera);
    }
}
