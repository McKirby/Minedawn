package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityZombie extends EntityLiving {
    protected EntityZombie(Level level, Vector2f pos) {
        super(EntityType.ZOMBIE, level, pos, 0.8f);
        super.setMaxSpeed(2.5f);

        super.goalSelector.addGoal(new EntityGoalRandomStroll(1, this, 1.0f, 0.1f, 3));
        super.goalSelector.addGoal(new EntityGoalMoveTowardsTarget<>(10, this, 0.0f, 1.0f, 10.0f, EntityPlayer.class));
    }
}
