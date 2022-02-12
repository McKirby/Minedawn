package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.LevelObject;
import net.reindiegames.re2d.core.level.Pinpointable;
import org.joml.Vector2f;

public interface ProjectileSource extends Pinpointable, LevelObject {
    public default void shoot(Class<? extends EntityProjectile> clazz, Vector2f direction, float speed) {
        final EntityProjectile projectile = this.getLevel().spawn(clazz, this.getCenter());
        projectile.source = this;
        projectile.setMaxSpeed(speed);
        projectile.move(direction.x, direction.y);
    }
}
