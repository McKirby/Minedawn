package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.DamageSource;
import net.reindiegames.re2d.core.level.Damageable;
import net.reindiegames.re2d.core.level.ICollidable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public abstract class EntityMonster extends EntityLiving implements DamageSource {
    protected EntityMonster(EntityType type, Level level, Vector2f pos, boolean throttle, float size) {
        super(type, level, pos, throttle, size);
    }

    @Override
    public boolean isHostileTowards(Entity other) {
        return !(other instanceof EntityMonster);
    }

    @Override
    public void collision(ICollidable object) {
        if (object instanceof EntityLiving && this.isHostileTowards(((Entity) object))) {
            ((EntityLiving) object).damage(this);
        }
    }

    @Override
    public int getRawDamage(Damageable target) {
        return 5;
    }
}
