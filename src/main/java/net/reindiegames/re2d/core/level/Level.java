package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.Tickable;
import net.reindiegames.re2d.core.level.entity.EntitySentient;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.lang.reflect.Constructor;

public interface Level extends ChunkPopulator, Tickable {
    public static final int MOVEMENT_PRECISION = CoreParameters.TICK_RATE;
    public static final int VELOCITY_PRECISION = CoreParameters.TICK_RATE;

    public abstract ChunkBase getChunkBase();

    public abstract Vector2i getSpawn();

    public default Chunk getChunk(Vector2f levelPos) {
        return this.getChunk(levelPos, false, false);
    }

    public default Chunk getChunk(Vector2f levelPos, boolean generate, boolean load) {
        final Vector2i chunkPos = CoordinateSystems.levelToChunk(levelPos);
        return this.getChunkBase().getChunk(chunkPos.x, chunkPos.y, generate, load);
    }

    public default Tile getTile(Vector2f levelPos) {
        final Chunk chunk = this.getChunk(levelPos, false, false);
        if (chunk == null) return null;

        final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
        return chunk.tiles[relative.x][relative.y];
    }

    public default void setTileType(Vector2f levelPos, TileType type) {
        this.setTileType(levelPos, type, type.defaultVariant);
    }

    public default void setTileType(Vector2f levelPos, TileType type, short variant) {
        final Chunk chunk = this.getChunk(levelPos, false, false);
        if (chunk == null) return;

        final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
        final Tile newTile = new Tile(this, (int) levelPos.x, (int) levelPos.y, type);
        newTile.variant = variant;

        final Tile oldTile = chunk.tiles[relative.x][relative.y];
        if (oldTile != null) {
            oldTile.dispose();
        }

        chunk.tiles[relative.x][relative.y] = newTile;
        chunk.changed = true;
    }

    public default <E extends EntitySentient> E spawn(Class<E> implClazz, Vector2i pos) {
        return this.spawn(implClazz, new Vector2f(pos.x, pos.y));
    }

    public default <E extends EntitySentient> E spawn(Class<E> implClazz, Vector2f pos) {
        Constructor<E> constructor;
        try {
            constructor = implClazz.getDeclaredConstructor(Level.class, Vector2f.class);
            constructor.setAccessible(true);

            E entity = constructor.newInstance(this, pos);
            this.getChunkBase().addEntity(entity);

            return entity;
        } catch (ReflectiveOperationException e) {
            Log.error("Can not spawn Entity (" + e.getMessage() + ")!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public default void syncTick(long totalTicks, float delta) {
        this.getChunkBase().syncTick(totalTicks, delta);
    }

    @Override
    public default void asyncTick(long totalTicks, float delta) {
        this.getChunkBase().asyncTick(totalTicks, delta);
    }
}
