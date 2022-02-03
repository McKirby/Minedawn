package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Collidable;
import net.reindiegames.re2d.core.level.Level;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;

public abstract class Entity extends Collidable {
    public static final float ENTITY_PADDING = 0.1f;

    public final EntityType type;
    public final int entityId;

    public short action;

    //TODO: This should not be in the Core Part. It is Client Code. Can this be fixed?
    @Deprecated
    public short state;

    protected Entity(EntityType type, Level level, Vector2f pos, Vector2f size) {
        super(level, pos, size, BodyType.DYNAMIC, 10.0f);
        this.type = type;
        this.entityId = level.getChunkBase().nextEntityId();

        level.getChunkBase().createBoundingSphere(body, 0.5f - ENTITY_PADDING);

        this.action = 0;
        this.state = 1;
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
