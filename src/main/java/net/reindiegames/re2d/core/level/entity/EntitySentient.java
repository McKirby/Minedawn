package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntitySentient extends Entity {
    protected EntitySentient(EntityType type, Level level, Vector2f pos, Vector2f size) {
        super(type, level, pos, size);
    }
}
