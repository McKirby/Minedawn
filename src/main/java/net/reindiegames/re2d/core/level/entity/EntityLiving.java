package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.DamageSource;
import net.reindiegames.re2d.core.level.Damageable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityLiving extends EntitySentient implements Damageable {
    private int maxHealth;
    private int health;
    protected int naturalRegeneration;

    private boolean combat;
    private boolean alwaysInCombat;
    private long lastCombatEntry;
    private long outOfCombatTicks;

    protected DamageSource lastDamageSource;
    protected long lastDamage;
    protected long damageInterval;

    protected EntityLiving(EntityType type, Level level, Vector2f pos, float size) {
        super(type, level, pos, size);
        this.maxHealth = 20;
        this.health = maxHealth;
        this.naturalRegeneration = 1;

        this.combat = false;
        this.alwaysInCombat = false;
        this.outOfCombatTicks = CoreParameters.TICK_RATE * 5;

        this.lastDamageSource = null;
        this.lastDamage = -1;
        this.damageInterval = (long) (0.5f * CoreParameters.TICK_RATE);
    }

    public boolean isDead() {
        return super.isDead() || health <= 0;
    }

    public float getHealthRatio() {
        return ((float) health) / ((float) maxHealth);
    }

    public void heal(int rawHealth) {
        this.health = Math.min(health + rawHealth, maxHealth);
    }

    public boolean isInCombat() {
        return combat || alwaysInCombat;
    }

    public void setInCombat(boolean combat) {
        this.combat = combat;
        if (combat) {
            this.lastCombatEntry = CoreParameters.totalTicks;
        }
    }

    public long getInCombatTicks() {
        if (this.isInCombat()) {
            return CoreParameters.totalTicks - lastCombatEntry;
        } else {
            return 0L;
        }
    }

    public boolean isAlwaysInCombat() {
        return alwaysInCombat;
    }

    public void setAlwaysInCombat(boolean alwaysInCombat) {
        if (alwaysInCombat) this.lastCombatEntry = CoreParameters.totalTicks;
        this.alwaysInCombat = alwaysInCombat;
    }

    @Override
    public void shoot(Class<? extends EntityProjectile> clazz, Vector2f direction, float speed) {
        super.shoot(clazz, direction, speed);
        this.setInCombat(true);
    }

    @Override
    public void syncTick(float delta) {
        super.syncTick(delta);

        long totalTicks = CoreParameters.totalTicks;
        if (this.isInCombat() && totalTicks - lastCombatEntry >= outOfCombatTicks) {
            this.setInCombat(false);
        }

        if (!this.isInCombat() && totalTicks % CoreParameters.TICK_RATE == 0) {
            this.heal(naturalRegeneration);
        }
    }

    @Override
    public int damage(DamageSource source, int rawDamage) {
        if (CoreParameters.totalTicks - lastDamage < damageInterval) return 0;

        int damage = Math.min(health, rawDamage);
        this.health -= damage;
        this.setInCombat(damage > 0);

        this.lastDamage = CoreParameters.totalTicks;
        this.lastDamageSource = source;

        return damage;
    }

    @Override
    public boolean wasDamagedSince(float seconds) {
        return lastDamage > 0 && (CoreParameters.totalTicks - lastDamage) <= seconds * CoreParameters.TICK_RATE;
    }
}
