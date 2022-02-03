package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.ResourceLevel;
import org.joml.Vector2f;

abstract class RenderStage<S extends Shader, O> {
    protected final S shader;

    protected RenderStage(S shader) {
        this.shader = shader;
    }

    protected final void render(O renderSource, long window, Vector2f c, long totalTicks) {
        this.load();
        this.prepare(window, c);
        this.process(totalTicks, renderSource);
        this.finish();
        this.yield();
    }

    protected abstract void load();

    protected abstract void prepare(long window, Vector2f c);

    protected abstract void process(long totalTicks, O renderSource);

    protected abstract void finish();

    protected abstract void yield();
}

