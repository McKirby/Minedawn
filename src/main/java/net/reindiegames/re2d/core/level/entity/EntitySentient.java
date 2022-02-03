package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class EntitySentient extends Entity {
    public final Navigator navigator;

    protected EntitySentient(EntityType type, Level level, Vector2f pos, float size) {
        super(type, level, pos, size);
        this.navigator = new Navigator(this);
    }

    @Override
    public void asyncTick(long totalTicks, float delta) {
        super.asyncTick(totalTicks, delta);

        Vector2i nextWaypoint = navigator.nextWaypoint();
        if (nextWaypoint != null) {
            Vector2f pos = this.getCenter();
            float dx = (nextWaypoint.x + 0.5f) - pos.x;
            float dy = (nextWaypoint.y + 0.5f) - pos.y;
            this.move(dx, dy);

            if (new Vector2f(dx, dy).length() <= 0.1f) {
                if (navigator.progressIndex()) {
                    navigator.stopNavigation();
                }
            }
        }
    }
}
