package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.DamageSource;
import net.reindiegames.re2d.core.level.ICollidable;
import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityZombie extends EntityMonster implements DamageSource {
    protected EntityZombie(Level level, Vector2f pos) {
        super(EntityType.ZOMBIE, level, pos, true, 0.8f);

        super.goalSelector.add(new EntityGoalRandomStroll(1, this, 1.0f, 0.1f, 3));
        super.goalSelector.add(new EntityMoveToTargetGoal<>(10, this, 0.0f, 0.25f, EntityPlayer.class, 15.0f, 2.0f));
    }

    @Override
    public boolean collidesWith(ICollidable object) {
        if (object instanceof EntityZombie) return false;
        return super.collidesWith(object);
    }

    @Override
    public void collision(ICollidable object) {
        if(object instanceof EntityPlayer) {
            ((EntityPlayer) object).damage(this, 2);
        }
    }
}
