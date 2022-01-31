package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Level;
import org.joml.Vector2f;

public class EntityLiving extends EntitySentient {
    public int health;
    public int maxHealth;

    protected EntityLiving(EntityType type, Level level, Vector2f pos, Vector2f size, float rotation) {
        super(type, level, pos, size, rotation);
        this.maxHealth = 20;
        this.health = maxHealth;
    }

    public boolean isDead() {
        return health <= 0;
    }
}
