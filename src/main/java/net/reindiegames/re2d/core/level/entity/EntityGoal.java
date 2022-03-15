package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;

public abstract class EntityGoal implements Comparable<EntityGoal> {
    public final EntitySentient entity;
    public final int priority;

    protected long cooldownTicks;
    protected long lastYield;
    protected float selectChance;

    public EntityGoal(EntitySentient entity, int priority, float cooldownSeconds, float chance) {
        this.entity = entity;
        this.priority = priority;

        this.cooldownTicks = (int) (CoreParameters.TICK_RATE * cooldownSeconds);
        this.lastYield = -1L;
        this.selectChance = chance;
    }

    public boolean select(long totalTicks) {
        boolean c = lastYield == -1L || (totalTicks - lastYield) >= cooldownTicks;
        boolean r = selectChance != 0.0f && entity.random.nextInt((int) (1.0f / selectChance)) == 0;
        return c && r;
    }

    public abstract void execute(long totalTicks);

    public void yield(long totalTicks) {
        this.lastYield = totalTicks;
    }

    public abstract boolean isDone(long totalTicks);

    public void iterate(long totalTicks) {
    }

    @Override
    public int compareTo(EntityGoal o) {
        return Integer.compare(o.priority, this.priority);
    }
}
