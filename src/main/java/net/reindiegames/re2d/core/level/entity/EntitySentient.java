package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.ICollidable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class EntitySentient extends Entity {
    public final Random random;
    public final Navigator navigator;
    public final GoalSelector goalSelector;

    protected EntitySentient(EntityType type, Level level, Vector2f pos, float size) {
        super(type, level, pos, size);
        this.random = new Random();
        this.navigator = new Navigator(this);
        this.goalSelector = new GoalSelector(this);
    }

    @Override
    public void halt() {
        super.halt();
        navigator.stopNavigation();
    }

    @Override
    public void syncTick(long totalTicks, float delta) {
        super.syncTick(totalTicks, delta);
        goalSelector.select(totalTicks);
    }

    @Override
    public void asyncTick(long totalTicks, float delta) {
        Vector2i nextWaypoint = navigator.nextWaypoint();
        if (nextWaypoint != null) {
            Vector2f pos = this.getCenter();
            float dx = (nextWaypoint.x + 0.5f) - pos.x;
            float dy = (nextWaypoint.y + 0.5f) - pos.y;
            this.move(dx, dy);

            if (new Vector2f(dx, dy).length() <= 0.1f) {
                if (navigator.progressIndex()) {
                    this.halt();
                }
            }
        }
        super.asyncTick(totalTicks, delta);
    }

    @Override
    public void touch(ICollidable object, boolean collision) {
    }

    @Override
    public void release(ICollidable object, boolean collision) {
    }

    @Override
    public boolean collidesWith(ICollidable object) {
        return true;
    }

    @Override
    public void collision(ICollidable object) {
        goalSelector.cancel();
        this.halt();
    }
}
