package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.ACollidable;
import net.reindiegames.re2d.core.level.ICollidable;
import net.reindiegames.re2d.core.level.Level;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;

public abstract class Entity extends ACollidable {
    public static final float ENTITY_PADDING = 0.0f;

    public final EntityType type;
    public final int entityId;

    public long timeCreated;
    public boolean dead;
    public short action;
    public short actionState;

    protected Entity(EntityType type, Level level, Vector2f pos, float size) {
        super(level, pos, new Vector2f(size, size), BodyType.DYNAMIC, 10.0f);
        this.type = type;
        this.entityId = level.getChunkBase().nextEntityId();

        level.getChunkBase().createBoundingSphere(this, body, size, ENTITY_PADDING);

        this.timeCreated = CoreParameters.totalTicks;
        this.dead = false;
        this.action = 0;
        this.actionState = 0;
    }

    public boolean hasLineOfSight(Vector2f pos, float maxDistance, boolean invisibleEntities) {
        final Vector2f center = this.getCenter();
        if (center.distanceSquared(pos) > (maxDistance * maxDistance)) return false;

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
        }, new Vec2(center.x, center.y), new Vec2(pos.x, pos.y));

        return result[0];
    }

    public long getTimeExisted() {
        return CoreParameters.totalTicks - timeCreated;
    }

    public void die() {
        this.removePhysics();
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
