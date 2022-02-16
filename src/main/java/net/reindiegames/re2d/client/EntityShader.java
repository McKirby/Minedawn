package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Damageable;
import net.reindiegames.re2d.core.level.entity.Entity;

public class EntityShader extends LevelShader {
    protected static final String VERTEX_FILE = "net/reindiegames/re2d/client/entities.vert";
    protected static final String FRAGMENT_FILE = "net/reindiegames/re2d/client/entities.frag";

    protected int hurtLocation;

    protected EntityShader() throws IllegalArgumentException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void loadUniforms() {
        super.loadUniforms();
        this.hurtLocation = super.getUniformLocation("hurt");
    }

    public void loadEntity(Entity entity) {
        if (entity instanceof Damageable) {
            super.loadBoolean(hurtLocation, ((Damageable) entity).wasDamagedSince(0.1f));
        }
    }
}
