package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;

import static net.reindiegames.re2d.core.CoreParameters.TICK_RATE;
import static net.reindiegames.re2d.core.level.Level.VELOCITY_PRECISION;

public abstract class ACollidable extends Transformable implements Tickable, ICollidable {
    protected static final float SPEED_FACTOR = VELOCITY_PRECISION * ((float) TICK_RATE / (float) VELOCITY_PRECISION);

    public final Level level;
    public final Body body;

    private float defaultSpeedThrottle;
    private float maxSpeed;

    protected ACollidable(Level l, Vector2f pos, Vector2f size, BodyType type, float maxSpeed) {
        this(l, pos, size, type, 1.0f, maxSpeed);
    }

    protected ACollidable(Level l, Vector2f p, Vector2f s, BodyType t, float speedThrottle, float maxSpeed) {
        super(s);
        this.level = l;

        this.defaultSpeedThrottle = speedThrottle;
        this.maxSpeed = maxSpeed;

        if (t == null) {
            this.body = null;
        } else {
            this.body = l.getChunkBase().createBody(this, t, p.x, p.y);
            body.m_userData = this;
            body.m_linearDamping = speedThrottle;
        }
    }

    @Override
    public final Vector2f getPosition() {
        final Vec2 pos = body.getPosition();
        return new Vector2f(pos.x, pos.y);
    }

    public final Vector2f getVelocity() {
        final Vec2 velocity = body.getLinearVelocity();
        return new Vector2f(velocity.x, velocity.y);
    }

    public final float getSpeed() {
        return this.getVelocity().length() * SPEED_FACTOR;
    }

    protected final void clampVelocity() {
        if (this.getSpeed() > maxSpeed) {
            final Vec2 velocity = body.getLinearVelocity();
            velocity.normalize();
            body.setLinearVelocity(velocity.mul(maxSpeed / SPEED_FACTOR));
        }
    }

    protected final void throttleVelocity() {
        this.throttleVelocity(defaultSpeedThrottle);
    }

    protected final void throttleVelocity(float throttle) {
        body.m_linearDamping = throttle;
    }

    public final void move(float dx, float dy) {
        this.throttleVelocity(0.0f);

        final Vec2 dir = new Vec2(dx, dy);
        dir.normalize();
        body.applyLinearImpulse(dir.mul(maxSpeed), body.getPosition());
        this.clampVelocity();
    }

    public final void halt() {
        this.throttleVelocity();
    }

    public final boolean isMoving() {
        return this.getVelocity().lengthSquared() > 0;
    }

    @Override
    public final Level getLevel() {
        return level;
    }

    @Override
    public final Body getBody() {
        return body;
    }

    @Override
    public void syncTick(long totalTicks, float delta) {
    }

    @Override
    public void asyncTick(long totalTicks, float delta) {
        if (this.isMoving()) super.changed = true;
    }
}
