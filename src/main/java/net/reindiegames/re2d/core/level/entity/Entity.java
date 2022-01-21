package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Transformable;
import org.joml.Vector2f;

public class Entity extends Transformable {
    public final EntityType type;

    public Entity(EntityType type, Vector2f pos, Vector2f size, float rotation) {
        super(pos, size, rotation);
        this.type = type;
    }
}
