package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.ACollidable;
import net.reindiegames.re2d.core.level.Level;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;

public abstract class Entity extends ACollidable {
    public static final float ENTITY_PADDING = 0.0f;

    public final EntityType type;
    public final int entityId;

    public short action;
    public short actionState;

    protected Entity(EntityType type, Level level, Vector2f pos, float size) {
        super(level, pos, new Vector2f(size, size), BodyType.DYNAMIC, 10.0f);
        this.type = type;
        this.entityId = level.getChunkBase().nextEntityId();

        level.getChunkBase().createBoundingSphere(this, body, size, ENTITY_PADDING);

        this.action = 0;
        this.actionState = 0;
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
