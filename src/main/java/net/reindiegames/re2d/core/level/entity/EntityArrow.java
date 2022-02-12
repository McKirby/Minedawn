package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityArrow extends EntityProjectile {
    protected EntityArrow(Level level, Vector2f pos) {
        super(EntityType.ARROW, level, pos, 0.3f);
    }
}
