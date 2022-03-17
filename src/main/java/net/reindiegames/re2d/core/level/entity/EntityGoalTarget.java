package net.reindiegames.re2d.core.level.entity;

public abstract class EntityGoalTarget<E extends Entity> extends EntityGoal implements TargetReceiver<E> {
    protected final Class<E> targetClass;
    protected final float targetDistance;
    protected E target;

    public EntityGoalTarget(
            int prio, EntitySentient entity, float cooldown, float chance,
            Class<E> targetClass, float distance
    ) {
        super(prio, entity, cooldown, chance);
        this.targetClass = targetClass;
        this.targetDistance = distance;
        entity.targetSelector.add(this);
    }

    @Override
    public boolean select(long totalTicks) {
        return super.select(totalTicks) && this.hasTarget();
    }

    @Override
    public Class<E> getTargetClass() {
        return targetClass;
    }

    @Override
    public float getTargetDistance() {
        return targetDistance;
    }

    @Override
    public E getTarget() {
        return target;
    }

    @Override
    public void setTarget(Entity target) {
        this.target = (E) target;
    }

    @Override
    public void loseTarget() {
        this.target = null;
    }

    @Override
    public boolean offerTarget(Entity target) {
        return targetClass.isInstance(target);
    }
}
