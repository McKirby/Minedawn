package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.*;
import org.joml.Vector2f;

public class EntityProjectile extends EntityInsentient implements DamageSource {
    protected ProjectileSource source;
    protected long maxExistingTime;

    protected EntityProjectile(EntityType type, Level level, Vector2f pos, float size) {
        super(type, level, pos, size);
        super.setSpeedThrottle(0.0f);
        this.maxExistingTime = CoreParameters.TICK_RATE * 4;
        body.setBullet(true);
    }

    @Override
    public void touch(ICollidable object, boolean collision) {
        if (collision) {
            if (object instanceof Damageable) {
                ((Damageable) object).damage(this, 10);
            }
            this.die();
        }
    }

    @Override
    public void release(ICollidable object, boolean collision) {
    }

    @Override
    public boolean collidesWith(ICollidable object) {
        return (object instanceof EntitySentient && !object.equals(source)) || object instanceof Tile;
    }

    @Override
    public void collision(ICollidable object) {
    }

    @Override
    public void syncTick(float delta) {
        super.syncTick(delta);

        if (this.getTicksLived() > maxExistingTime) {
            this.die();
        }
    }
}
