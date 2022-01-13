package net.reindiegames.re2d.client.gl;

public abstract class RenderStage<S extends Shader> {
    protected final S shader;

    public RenderStage(S shader) {
        this.shader = shader;
    }

    public final void render(Camera camera, long window, long totalTicks, boolean debug) {
        this.load();
        this.prepare(camera, window);
        this.process(totalTicks, debug);
        this.finish();
        this.yield();
    }

    public abstract void load();

    public abstract void prepare(Camera camera, long window);

    public abstract void process(long totalTicks, boolean debug);

    public abstract void finish();

    public abstract void yield();
}

