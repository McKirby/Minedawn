package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.DamageSource;
import net.reindiegames.re2d.core.level.Damageable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityLiving extends EntitySentient implements Damageable {
    public int health;
    public int maxHealth;

    protected EntityLiving(EntityType type, Level level, Vector2f pos, float size) {
        super(type, level, pos, size);
        this.maxHealth = 20;
        this.health = maxHealth;
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void syncTick(float delta) {
        super.syncTick(delta);
        super.dead = this.isDead();
    }

    @Override
    public int damage(DamageSource source, int rawDamage) {
        int damage = Math.min(health, rawDamage);
        this.health -= damage;
        return damage;
    }
}
