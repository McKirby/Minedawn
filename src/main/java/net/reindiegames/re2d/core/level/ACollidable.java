package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.Joint;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

import static net.reindiegames.re2d.core.CoreParameters.TICK_RATE;
import static net.reindiegames.re2d.core.level.Level.VELOCITY_PRECISION;

public abstract class ACollidable extends Transformable implements Tickable, ICollidable {
    protected static final float SPEED_FACTOR = VELOCITY_PRECISION * ((float) TICK_RATE / (float) VELOCITY_PRECISION);

    public final Level level;
    public final Body body;

    private final List<Joint> joints;
    private float defaultSpeedThrottle;
    private float maxSpeed;

    protected ACollidable(Level l, Vector2f pos, Vector2f size, BodyType type, float maxSpeed) {
        this(l, pos, size, type, 1.0f, maxSpeed);
    }

    protected ACollidable(Level l, Vector2f p, Vector2f s, BodyType t, float speedThrottle, float maxSpeed) {
        super(s);
        this.level = l;

        this.joints = new ArrayList<>();
        this.defaultSpeedThrottle = speedThrottle;
        this.maxSpeed = maxSpeed;

        this.body = l.getChunkBase().createBody(this, t, p.x, p.y);
        body.m_userData = this;
        body.m_linearDamping = speedThrottle;
        body.m_mass = s.x * s.y;
    }

    public void setSpeedThrottle(float throttle) {
        body.m_linearDamping = throttle;
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

    public void setMaxSpeed(float speed) {
        this.maxSpeed = Math.max(0.0f, speed);
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

    public boolean moveTowards(Vector2i waypoint, float reachThreshold) {
        Vector2f pos = this.getCenter();
        float dx = (waypoint.x + 0.5f) - pos.x;
        float dy = (waypoint.y + 0.5f) - pos.y;
        this.move(dx, dy);

        return new Vector2f(dx, dy).length() <= reachThreshold;
    }

    public final void move(float dx, float dy) {
        this.throttleVelocity(0.0f);

        final Vec2 dir = new Vec2(dx, dy);
        dir.normalize();
        body.applyLinearImpulse(dir.mul(maxSpeed), body.getPosition());
        this.clampVelocity();
    }

    public void halt() {
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
    public void syncTick(float delta) {
    }

    @Override
    public void asyncTick(float delta) {
        if (this.isMoving()) super.changed = true;
    }
}
