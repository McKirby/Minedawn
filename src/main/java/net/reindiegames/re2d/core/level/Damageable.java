package net.reindiegames.re2d.core.level;

public interface Damageable {
    public abstract int damage(DamageSource source, int rawDamage);

    public abstract boolean wasDamagedSince(float seconds);

    public abstract boolean wasDamagedSince(DamageSource source, float seconds);
}
