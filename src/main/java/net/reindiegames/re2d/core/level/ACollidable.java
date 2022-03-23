package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.joml.Vector2i;

import static net.reindiegames.re2d.core.CoreParameters.TICK_RATE;
import static net.reindiegames.re2d.core.level.Level.VELOCITY_PRECISION;

public abstract class ACollidable extends Transformable implements Tickable, ICollidable {
    protected static final float SPEED_FACTOR = VELOCITY_PRECISION * ((float) TICK_RATE / (float) VELOCITY_PRECISION);

    public final Level level;
    public final Body body;

    private float maxSpeed;

    protected ACollidable(Level l, Vector2f p, Vector2f s, BodyType t, boolean throttle, float maxSpeed) {
        super(s);
        this.level = l;

        this.body = l.getChunkBase().createBody(this, t, p.x, p.y);
        body.m_userData = this;
        body.m_mass = s.x * s.y;
        body.m_linearDamping = throttle ? 1.0f : 0.0f;
    }

    @Override
    public final Vector2f getPosition() {
        final Vec2 pos = body.getPosition();
        return new Vector2f(pos.x, pos.y);
    }

    public final float getSpeed() {
        return this.getVelocity().length() * SPEED_FACTOR;
    }

    public final float getMaxSpeed() {
        return maxSpeed;
    }

    public final void setMaxSpeed(float speed) {
        this.maxSpeed = Math.max(0.0f, speed);
    }

    public final Vector2f getVelocity() {
        final Vec2 velocity = body.getLinearVelocity();
        return new Vector2f(velocity.x, velocity.y);
    }

    protected final void clampVelocity() {
        if (this.getSpeed() > maxSpeed) {
            final Vec2 velocity = body.getLinearVelocity();
            velocity.normalize();
            body.setLinearVelocity(velocity.mul(maxSpeed / SPEED_FACTOR));
        }
    }

    public boolean moveTowards(Vector2i waypoint, float reachThreshold) {
        Vector2f pos = this.getCenter();
        float dx = (waypoint.x + 0.5f) - pos.x;
        float dy = (waypoint.y + 0.5f) - pos.y;
        this.move(dx, dy);

        return new Vector2f(dx, dy).length() <= reachThreshold;
    }

    public final void move(float dx, float dy) {
        this.slide();

        final Vec2 dir = new Vec2(dx, dy);
        dir.normalize();
        body.applyLinearImpulse(dir.mul(maxSpeed), body.getPosition());
        this.clampVelocity();
    }

    public void halt() {
        body.m_linearDamping = 1.0f;
    }

    public void slide() {
        body.m_linearDamping = 0.0f;
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
    public void syncTick(float delta) {
    }

    @Override
    public void asyncTick(float delta) {
        if (this.isMoving()) {
            super.changed = true;
        }
    }
}
