package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.ACollidable;
import net.reindiegames.re2d.core.level.ICollidable;
import net.reindiegames.re2d.core.level.Level;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.joml.Random;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class Entity extends ACollidable {
    public static final float ENTITY_PADDING = 0.0f;

    public final EntityType type;
    public final int entityId;
    public final Fixture fixture;
    public final Random random;

    public long timeCreated;
    public short action;
    public short actionState;

    public long idleTicks;
    private boolean dead;

    protected Entity(EntityType type, Level level, Vector2f pos, boolean throttle, float size) {
        super(level, pos, new Vector2f(size, size), BodyType.DYNAMIC, throttle, 10.0f);
        this.type = type;
        this.entityId = level.getChunkBase().nextEntityId();
        this.fixture = level.getChunkBase().createBoundingSphere(this, body, size, ENTITY_PADDING);
        this.random = new Random();

        this.timeCreated = CoreParameters.totalTicks;
        this.action = 0;
        this.actionState = 0;

        this.idleTicks = 0L;
        this.dead = false;
        this.setMaxSpeed(type.speed);
    }

    public <E extends Entity> Stream<E> visibleEntities(Class<E> type, float distance) {
        final Vector2f center = this.getCenter();
        return this.visibleEntities(type, distance, (e1, e2) -> {
            return Float.compare(e1.getCenter().distanceSquared(center), e2.getCenter().distance(center));
        });
    }

    public <E extends Entity> Stream<E> visibleEntities(Class<E> type, float distance, Comparator<E> comparator) {
        final List<E> found = new ArrayList<>();
        level.getChunkBase().forEachEntity(entity -> {
            if (entity.equals(Entity.this)) return;

            if (type.isInstance(entity) && this.hasLineOfSight(entity, distance, false)) {
                found.add((E) entity);
            }
        });

        if (comparator != null) found.sort(comparator);
        return found.stream();
    }

    public boolean hasLineOfSight(Entity entity, float maxDistance, boolean full) {
        final Vector2f[] own = new Vector2f[] {
                this.getTopRight(),
                this.getTopLeft(),
                this.getBottomRight(),
                this.getBottomLeft(),
        };
        final Vector2f[] other = new Vector2f[] {
                entity.getTopRight(),
                entity.getTopLeft(),
                entity.getBottomRight(),
                entity.getBottomLeft(),
        };

        for (Vector2f vector2f : own) {
            for (Vector2f f : other) {
                if (this.hasLineOfSight(vector2f, f, maxDistance, true)) {
                    if (!full) return true;
                } else {
                    if (full) return false;
                }
            }
        }
        return full;
    }

    public boolean hasLineOfSight(Vector2f anchor, Vector2f pos, float maxDistance, boolean invisibleEntities) {
        if (anchor.distanceSquared(pos) > (maxDistance * maxDistance)) return false;

        boolean[] result = new boolean[1];
        result[0] = true;

        this.getLevel().getChunkBase().getPhysics().raycast((fixture, hit, normal, ratio) -> {
            ICollidable hitObject = (ICollidable) fixture.m_userData;
            if (invisibleEntities && hitObject instanceof Entity) {
                return 1.0f;
            }

            if (ICollidable.isCollision(hitObject, Entity.this)) {
                result[0] = false;
                return 0.0f;
            } else {
                return 1.0f;
            }
        }, new Vec2(anchor.x, anchor.y), new Vec2(pos.x, pos.y));

        return result[0];
    }

    public long getTicksLived() {
        return CoreParameters.totalTicks - timeCreated;
    }

    public boolean isDead() {
        return dead;
    }

    public final boolean isAlive() {
        return !this.isDead();
    }

    public void die() {
        this.dead = true;
    }

    public boolean isFeatured() {
        return false;
    }

    @Override
    public void syncTick(float delta) {
        this.idleTicks = this.isMoving() ? 0 : idleTicks + 1;
        super.syncTick(delta);
    }

    @Override
    public int hashCode() {
        return entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Entity) {
            return ((Entity) o).entityId == this.entityId;
        } else {
            return false;
        }
    }
}
