package net.reindiegames.re2d.core.level.entity;

public abstract class
EntityTargetGoal<E extends Entity> extends EntityGoal {
    protected final Class<E> type;
    protected final float distance;

    protected E target;

    public EntityTargetGoal(
            int prio, EntitySentient entity, float cooldown, float chance,
            float distance, Class<E> type
    ) {
        super(entity, prio, cooldown, chance);
        this.type = type;
        this.distance = distance;
    }

    public abstract void targetAcquired();

    public boolean hasTarget() {
        return target != null && target.isAlive();
    }

    public void loseTarget() {
        this.target = null;
    }

    @Override
    public boolean select(long totalTicks) {
        if (!super.select(totalTicks)) return false;
        if (this.hasTarget()) return false;

        entity.visibleEntities(type, distance, false).findFirst().ifPresent(target -> {
            this.target = target;
        });
        return this.hasTarget();
    }

    @Override
    public void execute(long totalTicks) {
    }

    @Override
    public void yield(long totalTicks) {
        super.yield(totalTicks);
        this.loseTarget();
    }

    @Override
    public boolean isDone(long totalTicks) {
        return !this.hasTarget();
    }

    @Override
    public void iterate(long totalTicks) {
        if (entity.hasLineOfSight(target, distance, false)) {
            this.targetAcquired();
        } else {
            this.loseTarget();
        }
    }
}
