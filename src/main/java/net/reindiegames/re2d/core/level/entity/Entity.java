package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.Transformable;
import org.joml.Vector2f;

public abstract class Entity extends Transformable {
    public final EntityType type;
    public final Level level;
    public final int entityId;

    public short state;

    protected Entity(EntityType type, Level level, Vector2f pos, Vector2f size, float rotation) {
        super(pos, size, rotation);
        this.type = type;
        this.level = level;
        this.entityId = level.getChunkBase().nextEntityId();
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
