package net.reindiegames.re2d.core.level.entity;

public interface TargetReceiver<E extends Entity> {
    public abstract Class<E> getTargetClass();

    public abstract float getTargetDistance();

    public abstract E getTarget();

    public abstract void setTarget(Entity target);

    public default boolean hasTarget() {
        return this.getTarget() != null && this.getTarget().isAlive();
    }

    public abstract void loseTarget();

    public abstract boolean offerTarget(Entity target);
}
