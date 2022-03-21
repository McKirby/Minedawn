package net.reindiegames.re2d.core.level.entity;

public class EntityMoveToTargetGoal<E extends Entity> extends EntityTargetGoal<E> {
    protected float speedFactor;

    public EntityMoveToTargetGoal(
            int prio, EntitySentient entity, float cooldown, float chance,
            Class<E> targetClass, float distance, float speedFactor
    ) {
        super(prio, entity, cooldown, chance, targetClass, distance);
        this.speedFactor = speedFactor;
    }

    @Override
    public void execute(long totalTicks) {
        super.execute(totalTicks);
        entity.setMaxSpeed(entity.getBaseMaxSpeed() * speedFactor);
        ((EntityLiving) entity).setAlwaysInCombat(true);
    }

    @Override
    public void iterate(long totalTicks) {
        if (this.hasTarget()) {
            if (entity.hasLineOfSight(target, targetDistance, true)) {
                if (entity.navigator.isNavigating()) entity.halt();
                entity.moveTowards(target.getCenterTilePosition(), 0.1f);
            } else {
                if (!entity.navigator.isNavigating()) {
                    entity.navigator.navigate(target.getCenter(), (int) (targetDistance * 2.0f));
                }
            }
        }
    }

    @Override
    public boolean isDone(long totalTicks) {
        return !this.hasTarget() && !entity.navigator.isNavigating();
    }

    @Override
    public void yield(long totalTicks) {
        super.yield(totalTicks);
        entity.resetMaxSpeed();
        ((EntityLiving) entity).setAlwaysInCombat(false);
    }

    @Override
    public void loseTarget() {
        super.loseTarget();
        entity.navigator.navigate(lastSeen, (int) (targetDistance * 2.0f));
    }
}
