package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public abstract class Collidable extends Transformable implements Tickable {
    public static final byte NO_COLLISION = 0;
    public static final byte STATIC_COLLISION = 1;
    public static final byte DYNAMIC_COLLISION = 2;

    public final Level level;
    protected final Body body;

    public Collidable(Level level, Vector2f pos, Vector2f size, float rotation, BodyType type) {
        super(pos, size, rotation);
        this.level = level;

        if (type == null) {
            this.body = null;
        } else {
            this.body = level.getChunkBase().createBody(type, pos.x, pos.y);
        }
    }

    public Vector2f getVelocity() {
        final Vec2 velocity = body.getLinearVelocity();
        return new Vector2f(velocity.x, velocity.y);
    }

    public void move(float dx, float dy) {
        body.applyLinearImpulse(new Vec2(dx, dy), body.getPosition());
    }

    @Override
    public void syncTick(long totalTicks, float delta) {
    }

    @Override
    public void asyncTick(long totalTicks, float delta) {
        final Vec2 bodyPos = body.getPosition();
        pos.x = bodyPos.x;
        pos.y = bodyPos.y;
        super.changed = true;
    }
}
