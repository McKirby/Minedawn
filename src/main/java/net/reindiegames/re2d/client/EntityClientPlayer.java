package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.GameContext;
import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.entity.EntityArrow;
import net.reindiegames.re2d.core.level.entity.EntityPlayer;
import net.reindiegames.re2d.core.level.entity.EntityZombie;
import org.joml.Vector2f;

import static net.reindiegames.re2d.client.ClientParameters.*;

public class EntityClientPlayer extends EntityPlayer {
    protected EntityClientPlayer(Level level, Vector2f pos) {
        super(level, pos);
    }

    @Override
    public void asyncTick(float delta) {
        float dx = 0.0f;
        float dy = 0.0f;

        if (Input.MOVE_NORTH.isPressed()) dy += 1.0f;
        if (Input.MOVE_SOUTH.isPressed()) dy -= 1.0f;
        if (Input.MOVE_EAST.isPressed()) dx += 1.0f;
        if (Input.MOVE_WEST.isPressed()) dx -= 1.0f;

        if (dx == 0.0f && dy == 0.0f) {
            if (!super.navigator.isNavigating()) {
                this.halt();
            }
        } else {
            super.navigator.stopNavigation();
            this.move(dx, dy);
        }

        if (Input.ZOOM_IN.isPressed()) {
            tileScale = Math.max(Math.min(tileScale - 1, MAX_TILE_PIXEL_SIZE), MIN_TILE_PIXEL_SIZE);
            tileScaleChanged = true;
        }
        if (Input.ZOOM_OUT.isPressed()) {
            tileScale = Math.max(Math.min(tileScale + 1, MAX_TILE_PIXEL_SIZE), MIN_TILE_PIXEL_SIZE);
            tileScaleChanged = true;
        }

        if (Input.SHOOT.isPressed()) {
            this.shoot(EntityArrow.class, Input.getCursorLevelPosition().sub(this.getCenter()).normalize());
        }
        super.asyncTick(delta);
    }

    @Override
    public void syncTick(float delta) {
        super.syncTick(delta);

        if(this.isDead()) {
            GameContext.getInstance().stop();
        }
    }
}
