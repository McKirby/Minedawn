package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.ResourceLevel;
import org.joml.Vector2f;

abstract class RenderStage<S extends Shader, O> {
    protected final S shader;

    protected RenderStage(S shader) {
        this.shader = shader;
    }

    protected final void render(O renderSource, long window, Vector2f c) {
        this.load();
        this.prepare(window, c);
        this.process(renderSource);
        this.finish();
        this.yield();
    }

    protected abstract void load();

    protected abstract void prepare(long window, Vector2f c);

    protected abstract void process(O renderSource);

    protected abstract void finish();

    protected abstract void yield();
}

