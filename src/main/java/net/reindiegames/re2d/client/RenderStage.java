package net.reindiegames.re2d.client;

abstract class RenderStage<S extends Shader> {
    protected final S shader;

    protected RenderStage(S shader) {
        this.shader = shader;
    }

    protected final void render(Camera camera, long window, long totalTicks, boolean debug) {
        this.load();
        this.prepare(camera, window);
        this.process(totalTicks, debug);
        this.finish();
        this.yield();
    }

    protected abstract void load();

    protected abstract void prepare(Camera camera, long window);

    protected abstract void process(long totalTicks, boolean debug);

    protected abstract void finish();

    protected abstract void yield();
}

