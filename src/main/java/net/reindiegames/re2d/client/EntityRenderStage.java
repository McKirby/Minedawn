package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.entity.EntityLiving;
import org.joml.Vector2f;

import static net.reindiegames.re2d.core.CoreParameters.totalTicks;

public class EntityRenderStage extends LevelRenderStage<EntityShader, Level> {
    protected Mesh redMesh;
    protected Mesh grayMesh;

    protected EntityRenderStage() {
        super(new EntityShader());
        this.redMesh = Mesh.create("red_health", TextureAtlas.TERRAIN_01.getTextureCoords(0, 5));
        this.grayMesh = Mesh.create("gray_health", TextureAtlas.TERRAIN_01.getTextureCoords(5, 5));
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

            //Render HealthBar
            if (entity instanceof EntityLiving) {
                float ratio = ((EntityLiving) entity).getHealthRatio();

                if (ratio < 1.0f) {
                    Vector2f pos, scale;
                    float thickness = 3.0f / TextureAtlas.SPRITE_PIXEL_SIZE;
                    float yOffset = entity.size.y + thickness;

                    float redWidth = entity.size.x * ratio;
                    float grayWidth = entity.size.x * (1.0f - ratio);
                    float[] transform = new float[16];

                    pos = entity.getPosition().add(0.0f, yOffset, new Vector2f());
                    scale = new Vector2f(redWidth, thickness);
                    Shader.generateTransformation(transform, pos, scale, 0.0f);
                    this.renderMesh(transform, redMesh);

                    pos = entity.getPosition().add(redWidth, yOffset, new Vector2f());
                    scale = new Vector2f(grayWidth, thickness);
                    Shader.generateTransformation(transform, pos, scale, 0.0f);
                    this.renderMesh(transform, grayMesh);
                }
            }

            //Render Entity
            shader.loadEntity(entity);
            this.renderMesh(entity, compound.sprites[entity.action][frame]);
        });
    }
}
