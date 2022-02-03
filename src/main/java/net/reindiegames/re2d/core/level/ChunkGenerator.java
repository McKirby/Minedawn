package net.reindiegames.re2d.core.level;

import org.joml.Vector2i;

public interface ChunkGenerator extends ChunkPopulator {
    public abstract Vector2i getSpawn();

    public abstract void initialize(GeneratedLevel generatedLevel);
}
