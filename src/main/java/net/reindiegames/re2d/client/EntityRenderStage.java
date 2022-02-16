package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Level;

import static net.reindiegames.re2d.core.CoreParameters.totalTicks;

public class EntityRenderStage extends LevelRenderStage<EntityShader, Level> {
    protected EntityRenderStage() {
        super(new EntityShader());
    }

    @Override
    protected void process(Level level) {
        shader.loadTextureBank(0);
        shader.loadDepth(1.0f);
        level.getChunkBase().forEachEntity(entity -> {
            RenderCompound compound = ClientCoreBridge.ENTITY_COMPOUND_MAP.get(entity.type.id);

            RenderCompound.AnimationParameters p = compound.animation[entity.action];
            int frame;
            if (entity.actionState >= 0) {
                frame = entity.actionState;
            } else {
                frame = p.frames == 1 ? 0 : (int) ((totalTicks / p.ticks) % p.frames);
            }

            shader.loadEntity(entity);
            this.renderMesh(entity, compound.sprites[entity.action][frame]);
        });
    }
}
