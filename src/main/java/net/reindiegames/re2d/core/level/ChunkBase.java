package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;
import net.reindiegames.re2d.core.level.entity.Entity;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.JointDef;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Consumer;

import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class ChunkBase implements Tickable {
    public final Level level;
    protected final World world;

    private final Map<Integer, Map<Integer, Chunk>> chunkMap;
    private final Set<Chunk> loadedChunks;

    private final Set<Entity> createdEntities;
    private final Set<Entity> entities;
    private final Set<Entity> deadEntities;
    private final Set<TileEntity> tileEntities;
    private final List<JointDef> createdJoints;

    private int nextEntityId;
    private int nextTileEntityId;

    protected ChunkBase(Level level) {
        this.level = level;

        this.chunkMap = new HashMap<>();
        this.loadedChunks = new HashSet<>();

        this.createdEntities = new HashSet<>();
        this.entities = new HashSet<>();
        this.deadEntities = new HashSet<>();
        this.tileEntities = new HashSet<>();
        this.createdJoints = new ArrayList<>();

        this.world = new World(new Vec2(0.0f, 0.0f));

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                ICollidable a = (ICollidable) contact.m_fixtureA.m_userData;
                ICollidable b = (ICollidable) contact.m_fixtureB.m_userData;

                a.touch(b, a.collidesWith(b) && b.collidesWith(a));
                b.touch(a, a.collidesWith(b) && b.collidesWith(a));
            }

            @Override
            public void endContact(Contact contact) {
                ICollidable a = (ICollidable) contact.m_fixtureA.m_userData;
                ICollidable b = (ICollidable) contact.m_fixtureB.m_userData;

                a.release(b, a.collidesWith(b) && b.collidesWith(a));
                b.release(a, a.collidesWith(b) && b.collidesWith(a));
            }

            @Override
            public void preSolve(Contact contact, Manifold manifold) {
                ICollidable a = (ICollidable) contact.m_fixtureA.m_userData;
                ICollidable b = (ICollidable) contact.m_fixtureB.m_userData;

                boolean enabled = ICollidable.isCollision(a, b);
                contact.setEnabled(enabled);

                if (enabled) {
                    a.collision(b);
                    b.collision(a);
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {
            }
        });

        this.nextEntityId = 0;
        this.nextTileEntityId = 0;
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
                Vector2i tilePos;
                TileType type;
                Tile tile;
                for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
                    for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                        levelPos = CoordinateSystems.chunkRelativeToLevel(chunk.cx, chunk.cy, rx, ry);
                        tilePos = CoordinateSystems.levelToLevelTile(levelPos);
                        if (tiles[rx][ry] <= 0) continue;

                        type = TileType.getById(tiles[rx][ry]);
                        tile = type.newInstance(level, chunk, tilePos.x, tilePos.y);
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

    public Body createBody(ICollidable source, BodyType type, float tx, float ty) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position = new Vec2(tx, ty);
        bodyDef.userData = source;
        return world.createBody(bodyDef);
    }

    public Fixture createBoundingBox(ICollidable source, Body body, float width, float height, float padding) {
        float hw = width / 2.0f;
        float hh = height / 2.0f;

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw - padding, hh - padding, new Vec2(hw, hh), 0.0f);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.userData = source;
        return body.createFixture(fixtureDef);
    }

    public Fixture createBoundingSphere(ICollidable source, Body body, float diameter, float padding) {
        float radius = diameter / 2.0f;

        final CircleShape shape = new CircleShape();
        shape.setRadius(radius - padding);
        shape.m_p.x = radius;
        shape.m_p.y = radius;

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.userData = source;
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

    public void addEntity(Entity entity) {
        createdEntities.add(entity);
    }

    public void removeEntity(Entity entity) {
        deadEntities.add(entity);
    }

    public int nextTileEntityId() {
        return nextTileEntityId++;
    }

    public void forEachTileEntity(Consumer<TileEntity> tileEntityConsumer) {
        synchronized (tileEntities) {
            tileEntities.forEach(tileEntityConsumer);
        }
    }

    public void addTileEntity(TileEntity tileEntity) {
        synchronized (tileEntities) {
            tileEntities.add(tileEntity);
        }
    }

    public void removeTileEntity(TileEntity tileEntity) {
        synchronized (tileEntities) {
            tileEntities.remove(tileEntity);
        }
    }

    public void createJoint(JointDef def) {
        synchronized (createdJoints) {
            createdJoints.add(def);
        }
    }

    public World getPhysics() {
        return world;
    }

    @Override
    public void syncTick(float delta) {
        this.forEachEntity(entity -> {
            entity.syncTick(delta);

            if (entity.isDead()) {
                entity.removePhysics();
                this.removeEntity(entity);
            }
        });

        entities.addAll(createdEntities);
        createdEntities.clear();

        entities.removeAll(deadEntities);
        deadEntities.clear();

        this.forEachTileEntity(tileEntity -> tileEntity.syncTick(delta));

        for (JointDef def : createdJoints) {
            world.createJoint(def);
        }
        createdJoints.clear();
    }

    @Override
    public void asyncTick(float delta) {
        world.step(delta, Level.VELOCITY_PRECISION, Level.MOVEMENT_PRECISION);
        this.forEachEntity(entity -> entity.asyncTick(delta));
    }

    public int getLoadedEntities() {
        return entities.size();
    }
}
