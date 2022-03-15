package net.reindiegames.re2d.core.level.entity;

import org.joml.Vector2f;

public class EntityGoalMoveTowardsTarget<E extends Entity> extends EntityTargetGoal<E> {
    private E lastTarget;
    private Vector2f lastSeen;

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
    public void loseTarget(byte reason) {
        System.out.println(reason);
        if (reason == OUT_OF_SIGHT) {
            lastTarget = target;
            lastSeen = target.getCenter();
            entity.navigator.navigate(lastSeen, (int) distance);
        } else {
            entity.halt();
        }
        super.loseTarget(reason);
    }

    @Override
    public void yield(long totalTicks) {
        super.yield(totalTicks);
        this.lastTarget = null;
        this.lastSeen = null;
    }

    @Override
    public boolean isDone(long totalTicks) {
        return super.isDone(totalTicks) && !entity.navigator.isNavigating();
    }

    @Override
    public void iterate(long totalTicks) {
        if (super.hasTarget()) {
            if (entity.hasLineOfSight(target, distance, true)) {
                if (entity.navigator.isNavigating()) entity.halt();
                this.targetAcquired();
                return;
            }

            if (entity.hasLineOfSight(target, distance, false)) {
                if (!entity.navigator.isNavigating()) {
                    if (!entity.navigator.navigate(target.getCenter(), (int) (distance * distance))) {
                        this.loseTarget(OUT_OF_REACH);
                    }
                }
            } else {
                this.loseTarget(OUT_OF_SIGHT);
            }
        } else {
            if (entity.hasLineOfSight(lastTarget, distance, true)) {
                entity.halt();
                this.fixTarget(lastTarget);

                this.lastTarget = null;
                this.lastSeen = null;
            }
        }
    }
}
