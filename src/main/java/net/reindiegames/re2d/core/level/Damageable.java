package net.reindiegames.re2d.core.level;

public interface Damageable {
    public abstract int damage(DamageSource source, int rawDamage);

    public default void damage(DamageSource source) {
        this.damage(source, source.getRawDamage(this));
    }

    public abstract boolean wasDamagedSince(float seconds);

    public abstract boolean wasDamagedSince(DamageSource source, float seconds);
}
