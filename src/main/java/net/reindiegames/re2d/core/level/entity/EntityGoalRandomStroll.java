package net.reindiegames.re2d.core.level.entity;

import org.joml.Vector2f;
import org.joml.Vector2i;

public class EntityGoalRandomStroll extends EntityGoal {
    protected int range;

    public EntityGoalRandomStroll(int prio, EntitySentient entity, float cooldown, float chance, int range) {
        super(entity, prio, cooldown, chance);
        this.range = range;
    }

    @Override
    public boolean select(long totalTicks) {
        return super.select(totalTicks) && !entity.navigator.isNavigating();
    }

    @Override
    public void execute(long totalTicks) {
        final Vector2i pos = entity.getCenterTilePosition();
        int goalX = pos.x + entity.random.nextInt(range) * (entity.random.nextFloat() < 0.5f ? 1 : -1);
        int goalY = pos.y + entity.random.nextInt(range) * (entity.random.nextFloat() < 0.5f ? 1 : -1);

        entity.navigator.navigate(new Vector2f(goalX, goalY), range);
    }

    @Override
    public boolean isDone(long totalTicks) {
        return !entity.navigator.isNavigating();
    }
}
