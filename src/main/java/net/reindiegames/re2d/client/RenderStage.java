package net.reindiegames.re2d.client;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static net.reindiegames.re2d.client.ClientParameters.windowHeight;
import static net.reindiegames.re2d.client.ClientParameters.windowWidth;

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

