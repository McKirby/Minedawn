package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.DamageSource;
import net.reindiegames.re2d.core.level.Damageable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

public abstract class EntityLiving extends EntitySentient implements Damageable {
    public static final float INVULNERABLE_SECONDS_AFTER_DAMAGE = 1.0f;

    private int maxHealth;
    private int health;
    protected int naturalRegeneration;
    protected boolean invulnerable;

    private boolean combat;
    private boolean stayInCombat;
    private long lastCombatEntry;
    private long outOfCombatTicks;

    protected DamageSource lastDamageSource;
    protected Map<Integer, Long> lastEntityDamage;
    protected long lastDamage;
    protected long damageGracePeriod;

    protected EntityLiving(EntityType type, Level level, Vector2f pos, boolean throttle, float size) {
        super(type, level, pos, throttle, size);
        this.maxHealth = 20;
        this.health = maxHealth;
        this.naturalRegeneration = 1;
        this.invulnerable = false;

        this.combat = false;
        this.stayInCombat = false;
        this.lastCombatEntry = -1L;
        this.outOfCombatTicks = CoreParameters.TICK_RATE * 5;

        this.lastDamageSource = null;
        this.lastEntityDamage = new HashMap<>();
        this.lastDamage = -1L;
        this.damageGracePeriod = (long) (INVULNERABLE_SECONDS_AFTER_DAMAGE * CoreParameters.TICK_RATE);
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

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public final boolean isInCombat() {
        return combat;
    }

    public final void setInCombat(boolean c) {
        this.setInCombat(c, stayInCombat);
    }

    public final void setInCombat(boolean c, boolean stay) {
        this.combat = c;
        this.setStayInCombat(stay);

        if (c) {
            this.lastCombatEntry = CoreParameters.totalTicks;
        }
    }

    public final void setStayInCombat(boolean stayInCombat) {
        this.stayInCombat = stayInCombat;
    }

    public final long getInCombatTicks() {
        if (this.isInCombat()) {
            return CoreParameters.totalTicks - lastCombatEntry;
        } else {
            return 0L;
        }
    }

    public final boolean staysInCombat() {
        return stayInCombat;
    }

    @Override
    public void shoot(Class<? extends EntityProjectile> clazz, Vector2f direction) {
        super.shoot(clazz, direction);
        this.setInCombat(true);
    }

    @Override
    public void syncTick(float delta) {
        super.syncTick(delta);

        long totalTicks = CoreParameters.totalTicks;
        if (this.isInCombat()) {
            this.idleTicks = 0L;

            if (!stayInCombat && (totalTicks - lastCombatEntry >= outOfCombatTicks)) {
                this.setInCombat(false);
            }
        } else {
            if (totalTicks % CoreParameters.TICK_RATE == 0) {
                this.heal(naturalRegeneration);
            }
        }
    }

    @Override
    public int damage(DamageSource s, int rawDamage) {
        if (this.isInvulnerable()) return 0;

        long last = (s instanceof Entity) ? lastEntityDamage.getOrDefault(((Entity) s).entityId, -1L) : lastDamage;
        if (CoreParameters.totalTicks - last < damageGracePeriod) return 0;

        int damage = Math.min(health, rawDamage);
        this.health -= damage;
        this.setInCombat(damage > 0);

        if (s instanceof Entity) {
            lastEntityDamage.put(((Entity) s).entityId, CoreParameters.totalTicks);
        }
        this.lastDamage = CoreParameters.totalTicks;
        this.lastDamageSource = s;

        return damage;
    }

    @Override
    public boolean wasDamagedSince(float seconds) {
        if (lastDamage < 0) return false;
        return CoreParameters.totalTicks - lastDamage <= seconds * CoreParameters.TICK_RATE;
    }

    @Override
    public boolean wasDamagedSince(DamageSource source, float seconds) {
        long last = lastEntityDamage.getOrDefault((source instanceof Entity) ? ((Entity) source).entityId : -1, -1L);
        if (last < 0) return false;
        return CoreParameters.totalTicks - last <= seconds * CoreParameters.TICK_RATE;
    }
}
