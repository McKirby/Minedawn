package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.ResourceLevel;

class LevelRenderPipeline {
    private final TerrainRenderStage terrainRenderStage;

    protected LevelRenderPipeline() {
        this.terrainRenderStage = new TerrainRenderStage();
    }

    protected void render(ResourceLevel level, long window, float ctx, float cty, long totalTicks) {
        terrainRenderStage.render(level, window, ctx, cty, totalTicks);
    }
}
