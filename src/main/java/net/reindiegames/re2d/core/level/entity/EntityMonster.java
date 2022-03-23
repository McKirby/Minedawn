package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public abstract class EntityMonster extends EntityLiving {
    protected EntityMonster(EntityType type, Level level, Vector2f pos, boolean throttle, float size) {
        super(type, level, pos, throttle, size);
    }

    @Override
    public boolean isHostileTowards(Entity other) {
        return !(other instanceof EntityMonster);
    }
}
