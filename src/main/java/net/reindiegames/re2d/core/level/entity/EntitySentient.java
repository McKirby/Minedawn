package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.ICollidable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;
import org.joml.Vector2i;

public abstract class EntitySentient extends Entity implements ProjectileSource {
    public final TargetSelector targetSelector;
    public final Navigator navigator;
    public final GoalSelector goalSelector;

    protected long shootDelay;
    protected long lastShoot;

    protected EntitySentient(EntityType type, Level level, Vector2f pos, boolean throttle, float size) {
        super(type, level, pos, throttle, size);

        this.targetSelector = new TargetSelector(this);
        this.navigator = new Navigator(this);
        this.goalSelector = new GoalSelector(this);

        this.shootDelay = CoreParameters.TICK_RATE / 2L;
        this.lastShoot = 0L;
    }

    public abstract boolean isHostileTowards(Entity other);

    @Override
    public void shoot(Class<? extends EntityProjectile> clazz, Vector2f direction) {
        if (CoreParameters.totalTicks - lastShoot < shootDelay) return;
        ProjectileSource.super.shoot(clazz, direction);
        this.lastShoot = CoreParameters.totalTicks;
    }

    @Override
    public void halt() {
        super.halt();
        navigator.stopNavigation();
    }

    @Override
    public void asyncTick(float delta) {
        if (navigator.isNavigating()) {
            Vector2i waypoint = waypoint = navigator.nextWaypoint();

            if (waypoint != null) {
                this.idleTicks = 0L;
                if (this.moveTowards(waypoint, 0.1f)) {
                    if (navigator.progressIndex()) {
                        this.halt();
                    }
                }
            }
        }

        super.asyncTick(delta);
    }

    @Override
    public void syncTick(float delta) {
        super.syncTick(delta);

        targetSelector.select();
        if (goalSelector.select()) {
            this.idleTicks = 0L;
        }
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
        this.halt();
    }
}
