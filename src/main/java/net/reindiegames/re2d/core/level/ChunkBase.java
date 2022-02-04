package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;
import net.reindiegames.re2d.core.level.entity.Entity;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class ChunkBase implements Tickable {
    public final Level level;
    protected final World world;

    private final Map<Integer, Map<Integer, Chunk>> chunkMap;
    private final Set<Chunk> loadedChunks;
    private final Set<Entity> entities;

    private int nextEntityId;

    protected ChunkBase(Level level) {
        this.level = level;

        this.chunkMap = new HashMap<>();
        this.loadedChunks = new HashSet<>();
        this.entities = new HashSet<>();
        this.world = new World(new Vec2(0.0f, 0.0f));

        this.nextEntityId = 0;
    }

    public Chunk getChunk(int cx, int cy, boolean generate, boolean load) {
        synchronized (chunkMap) {
            Map<Integer, Chunk> xMap = chunkMap.computeIfAbsent(cx, key -> new HashMap<>());
            Chunk chunk = xMap.getOrDefault(cy, null);
            if (chunk != null) {
                if (load) this.loadChunk(chunk);
                return chunk;
            }

            if (generate) {
                chunk = new Chunk(level, cx, cy);

                final int[][] tiles = new int[CHUNK_SIZE][CHUNK_SIZE];
                final short[][] variants = new short[CHUNK_SIZE][CHUNK_SIZE];
                level.populate(chunk, tiles, variants);

                Vector2f levelPos;
                TileType type;
                Tile tile;
                for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
                    for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                        levelPos = CoordinateSystems.chunkRelativeToLevel(chunk.cx, chunk.cy, rx, ry);
                        if (tiles[rx][ry] <= 0) continue;

                        type = TileType.getById(tiles[rx][ry]);
                        tile = new Tile(chunk.level, (int) levelPos.x, (int) levelPos.y, type);
                        tile.variant = variants[rx][ry];

                        chunk.tiles[rx][ry] = tile;
                    }
                }

                xMap.put(cy, chunk);
                if (load) this.loadChunk(chunk);
                return chunk;
            } else {
                return null;
            }
        }
    }

    public Body createBody(BodyType type, float tx, float ty) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position = new Vec2(tx, ty);
        return world.createBody(bodyDef);
    }

    public Fixture createBoundingBox(Body body, float width, float height, float padding) {
        float hw = width / 2.0f;
        float hh = height / 2.0f;

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw - padding, hh - padding, new Vec2(hw, hh), 0.0f);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        return body.createFixture(fixtureDef);
    }

    public Fixture createBoundingSphere(Body body, float diameter, float padding) {
        float radius = diameter / 2.0f;

        final CircleShape shape = new CircleShape();
        shape.setRadius(radius - padding);
        shape.m_p.x = radius;
        shape.m_p.y = radius;

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        return body.createFixture(fixtureDef);
    }

    public void forEachLoadedChunk(Consumer<Chunk> chunkConsumer) {
        synchronized (loadedChunks) {
            loadedChunks.forEach(chunkConsumer);
        }
    }

    public void loadChunk(Chunk chunk) {
        synchronized (loadedChunks) {
            loadedChunks.add(chunk);
        }
    }

    public void unloadChunk(Chunk chunk) {
        synchronized (loadedChunks) {
            loadedChunks.remove(chunk);
        }
    }

    public int nextEntityId() {
        return nextEntityId++;
    }

    public void forEachEntity(Consumer<Entity> entityConsumer) {
        synchronized (entities) {
            entities.forEach(entityConsumer);
        }
    }

    protected void addEntity(Entity entity) {
        synchronized (entities) {
            entities.add(entity);
        }
    }

    private void removeEntity(Entity entity) {
        synchronized (entities) {
            entities.remove(entity);
        }
    }

    @Override
    public void syncTick(long totalTicks, float delta) {
        this.forEachEntity(entity -> entity.syncTick(totalTicks, delta));
    }

    @Override
    public void asyncTick(long totalTicks, float delta) {
        world.step(delta, Level.VELOCITY_PRECISION, Level.MOVEMENT_PRECISION);
        this.forEachEntity(entity -> entity.asyncTick(totalTicks, delta));
    }
}
