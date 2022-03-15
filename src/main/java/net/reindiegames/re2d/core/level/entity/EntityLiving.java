package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.DamageSource;
import net.reindiegames.re2d.core.level.Damageable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityLiving extends EntitySentient implements Damageable {
    public int health;
    public int maxHealth;

    protected DamageSource lastDamageSource;
    protected long lastDamage;

    protected EntityLiving(EntityType type, Level level, Vector2f pos, float size) {
        super(type, level, pos, size);
        this.maxHealth = 20;
        this.health = maxHealth;

        this.lastDamageSource = null;
        this.lastDamage = -1;
    }

    public boolean isDead() {
        return super.isDead() || health <= 0;
    }

    @Override
    public void syncTick(float delta) {
        super.syncTick(delta);
    }

    @Override
    public int damage(DamageSource source, int rawDamage) {
        int damage = Math.min(health, rawDamage);
        this.health -= damage;

        this.lastDamage = CoreParameters.totalTicks;
        this.lastDamageSource = source;

        return damage;
    }

    @Override
    public boolean wasDamagedSince(float seconds) {
        return lastDamage > 0 && (CoreParameters.totalTicks - lastDamage) <= seconds * CoreParameters.TICK_RATE;
    }
}
