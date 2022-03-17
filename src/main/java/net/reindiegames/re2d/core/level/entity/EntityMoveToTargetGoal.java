package net.reindiegames.re2d.core.level.entity;

public class EntityMoveToTargetGoal<E extends Entity> extends EntityTargetGoal<E> {
    public EntityMoveToTargetGoal(
            int prio, EntitySentient entity, float cooldown, float chance,
            Class<E> targetClass, float distance
    ) {
        super(prio, entity, cooldown, chance, targetClass, distance);
    }

    @Override
    public boolean isDone(long totalTicks) {
        return !this.hasTarget();
    }

    @Override
    public void iterate(long totalTicks) {
        entity.moveTowards(target.getCenterTilePosition(), 0.1f);
    }

    @Override
    public void loseTarget() {
        super.loseTarget();
        entity.halt();
    }
}
