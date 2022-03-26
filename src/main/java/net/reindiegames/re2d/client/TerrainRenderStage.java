package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.TileStack;

import java.util.HashMap;
import java.util.Map;

import static net.reindiegames.re2d.client.ClientCoreBridge.CHUNK_MESH_MAP;
import static net.reindiegames.re2d.core.CoreParameters.totalTicks;

class TerrainRenderStage extends LevelRenderStage<TerrainShader, Level> {
    protected TerrainRenderStage() {
        super(new TerrainShader());

    }

    @Override
    protected void process(Level level) {
        shader.loadTextureBank(0);
        shader.loadDepth(TileStack.LIQUID_LAYER);

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
    }
}
