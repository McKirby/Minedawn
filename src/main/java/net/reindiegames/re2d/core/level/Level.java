package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.Tickable;
import net.reindiegames.re2d.core.level.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.lang.reflect.Constructor;

public interface Level extends ChunkPopulator, Tickable {
    public static final int MOVEMENT_PRECISION = CoreParameters.TICK_RATE;
    public static final int VELOCITY_PRECISION = CoreParameters.TICK_RATE;

    public abstract ChunkBase getChunkBase();

    public abstract Vector2i getSpawn();

    public default Chunk getChunk(Vector2i tilePos, boolean generate, boolean load) {
        return this.getChunk(CoordinateSystems.levelTileToLevel(tilePos), generate, load);
    }

    public default Chunk getChunk(Vector2f levelPos, boolean generate, boolean load) {
        final Vector2i chunkPos = CoordinateSystems.levelToChunk(levelPos);
        return this.getChunkBase().getChunk(chunkPos.x, chunkPos.y, generate, load);
    }

    public default TileStack getTileStack(Vector2i tilePos) {
        return this.getTileStack(CoordinateSystems.levelTileToLevel(tilePos));
    }

    public default TileStack getTileStack(Vector2f levelPos) {
        final Chunk chunk = this.getChunk(levelPos, false, false);
        if (chunk == null) return null;

        final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
        return chunk.stacks[relative.x][relative.y];
    }

    public default void setTileType(Vector2i tilePos, byte targetLayer, boolean clear, TileType type) {
        this.setTileType(tilePos, targetLayer, clear, type, type.defaultVariant);
    }

    public default void setTileType(Vector2i tilePos, byte targetLayer, boolean clear, TileType type, short variant) {
        final Chunk chunk = this.getChunk(tilePos, false, false);
        if (chunk == null) return;

        final Vector2i relative = CoordinateSystems.levelToChunkRelative(tilePos);
        final TileStack stack = chunk.stacks[relative.x][relative.y];
        if(clear) stack.clear();
        stack.setTileType(targetLayer, type, variant);
        chunk.changed = true;
    }

    public default <E extends Entity> E spawn(Class<E> implClazz, Vector2i tilePos) {
        return this.spawn(implClazz, CoordinateSystems.levelTileToLevel(tilePos));
    }

    public default <E extends Entity> E spawn(Class<E> implClazz, Vector2f levelPos) {
        Constructor<E> constructor;
        try {
            constructor = implClazz.getDeclaredConstructor(Level.class, Vector2f.class);
            constructor.setAccessible(true);

            E entity = constructor.newInstance(this, levelPos);
            this.getChunkBase().addEntity(entity);

            return entity;
        } catch (ReflectiveOperationException e) {
            Log.error("Can not spawn Entity (" + e.getMessage() + ")!");
            e.printStackTrace();
            return null;
        }
    }

    public default int getLoadedEntities() {
        return this.getChunkBase().getLoadedEntities();
    }

    @Override
    public default void syncTick(float delta) {
        this.getChunkBase().syncTick(delta);
    }

    @Override
    public default void asyncTick(float delta) {
        this.getChunkBase().asyncTick(delta);
    }
}
