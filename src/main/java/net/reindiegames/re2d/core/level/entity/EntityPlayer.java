package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityPlayer extends EntityLiving {
    protected EntityPlayer(Level level, Vector2f pos) {
        super(EntityType.PLAYER, level, pos, 0.9f);
    }

    @Override
    public void asyncTick(long totalTicks, float delta) {
        super.asyncTick(totalTicks, delta);
    }
}
