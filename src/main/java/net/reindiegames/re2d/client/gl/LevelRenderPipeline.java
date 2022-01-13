package net.reindiegames.re2d.client.gl;

import net.reindiegames.re2d.core.GameContext;
import org.joml.Vector4f;

public class LevelRenderPipeline {
    public static final Vector4f CLEAR_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);

    private final TerrainRenderStage terrainRenderStage;

    public LevelRenderPipeline() {
        this.terrainRenderStage = new TerrainRenderStage();
    }

    public void render(Camera camera, long window, long totalTicks) {
        terrainRenderStage.render(camera, window, totalTicks, GameContext.debug);
    }
}
