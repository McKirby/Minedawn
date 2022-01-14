package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.level.Level;

abstract class RenderStage<S extends Shader> {
    protected final S shader;

    protected RenderStage(S shader) {
        this.shader = shader;
    }

    protected final void render(Level level, long window, float ctx, float cty, long totalTicks) {
        this.load();
        this.prepare(window, ctx, cty);
        this.process(totalTicks);
        this.finish();
        this.yield();
    }

    protected abstract void load();

    protected abstract void prepare(long window, float ctx, float cty);

    protected abstract void process(long totalTicks);

    protected abstract void finish();

    protected abstract void yield();
}

