package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.Transformable;
import org.joml.Vector2f;

public abstract class Entity extends Transformable {
    public final EntityType type;
    public final Level level;

    protected Entity(EntityType type, Level level, Vector2f pos, Vector2f size, float rotation) {
        super(pos, size, rotation);
        this.type = type;
        this.level = level;
    }
}
