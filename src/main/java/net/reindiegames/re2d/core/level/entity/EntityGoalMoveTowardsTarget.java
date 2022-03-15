package net.reindiegames.re2d.core.level.entity;

public class EntityGoalMoveTowardsTarget<E extends Entity> extends EntityTargetGoal<E> {
    public EntityGoalMoveTowardsTarget(
            int prio, EntitySentient entity, float cooldown, float chance,
            float distance, Class<E> type
    ) {
        super(prio, entity, cooldown, chance, distance, type);
    }

    @Override
    public void targetAcquired() {
        entity.moveTowards(target.getCenterTilePosition(), 0.1f);
    }

    @Override
    public void loseTarget() {
        super.loseTarget();
        entity.halt();
    }
}
