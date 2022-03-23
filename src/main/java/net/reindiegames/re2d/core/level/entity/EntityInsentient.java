package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public abstract class EntityInsentient extends Entity {
    protected EntityInsentient(EntityType type, Level level, Vector2f pos, boolean throttle, float size) {
        super(type, level, pos, throttle, size);
    }
}
