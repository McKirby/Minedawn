package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.meta.GameContext;

class LevelRenderPipeline {
    private final TerrainRenderStage terrainRenderStage;

    protected LevelRenderPipeline() {
        this.terrainRenderStage = new TerrainRenderStage();
    }

    protected void render(Camera camera, long window, long totalTicks) {
        terrainRenderStage.render(camera, window, totalTicks, GameContext.debug);
    }
}
