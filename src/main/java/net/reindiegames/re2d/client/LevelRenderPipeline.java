package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

class LevelRenderPipeline {
    private final TerrainRenderStage terrainRenderStage;

    protected LevelRenderPipeline() {
        this.terrainRenderStage = new TerrainRenderStage();
    }

    protected void render(Level level, long window, Vector2f c) {
        terrainRenderStage.render(level, window, c);
    }
}
