package net.reindiegames.re2d.core;

import net.reindiegames.re2d.client.gl.Camera;
import net.reindiegames.re2d.client.gl.LevelRenderPipeline;
import net.reindiegames.re2d.client.gl.Shader;
import net.reindiegames.re2d.client.gl.SpriteMesh;
import net.reindiegames.re2d.client.input.Input;
import net.reindiegames.util.Log;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryUtil.NULL;

public class GameContext {
    public static final int TICK_RATE = 20;
    public static final String TITLE = "Re2D";
    public static final int DEFAULT_HEIGHT = 800;
    public static final float DEFAULT_ASPECT_RATIO = 4.0f / 3.0f;
    public static final int DEFAULT_WIDTH = (int) (DEFAULT_HEIGHT * DEFAULT_ASPECT_RATIO);

    public static boolean debug = false;

    private final LevelRenderPipeline levelRenderPipeline;
    private final Camera camera;
    private long window;

    private GameContext() {
        Log.info("Loading Libraries...");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW!");

        Log.info("Initializing Window...");
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        this.window = GLFW.glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, TITLE, NULL, NULL);
        if (window == NULL) throw new IllegalStateException("Failed to create the GLFW Window!");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            int centerX = (vidmode.width() - pWidth.get(0)) / 2;
            int centerY = (vidmode.height() - pHeight.get(0)) / 2;
            GLFW.glfwSetWindowPos(window, centerX, centerY);
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        Log.info("Setting up OpenGL...");
        GL.createCapabilities();
        this.camera = new Camera();
        this.levelRenderPipeline = new LevelRenderPipeline();

        Log.info("Setting up Input...");
        Input.create(window);

        Input.EXIT_GAME.setAction((pressed -> {
            if (pressed) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        }));

        Input.DEBUG.setAction((pressed -> {
            if (pressed) {
                debug = !debug;
            }
        }));
    }

    public static void main(String[] args) {
        final GameContext context = new GameContext();
        context.start();
        context.dispose();
        Log.info("Bye!");
    }

    private final void tick(long totalTicks, float delta) {
    }

    private void start() {
        Log.info("Starting Game...");

        final long nsPerTick = (1000 * 1000 * 1000) / TICK_RATE;
        long now;
        long last = System.nanoTime();
        long diff;
        float delta = 0.0f;

        long lastDisplay = System.currentTimeMillis();
        long totalTicks = 0L;
        int ticks = 0;
        int frames = 0;

        while (!GLFW.glfwWindowShouldClose(window)) {
            now = System.nanoTime();
            diff = now - last;
            last = now;
            delta += ((float) diff) / nsPerTick;

            while (delta >= 1.0f) {
                this.tick(totalTicks, Math.min(delta, 1.0f));
                delta--;
                ticks++;
                totalTicks++;
            }

            GLFW.glfwPollEvents();
            levelRenderPipeline.render(camera, window, totalTicks);
            frames++;

            GLFW.glfwSwapBuffers(window);
            if (System.currentTimeMillis() - lastDisplay >= 1000) {
                Log.info("FPS: " + frames + ", TPS: " + ticks);
                frames = 0;
                ticks = 0;
                lastDisplay = System.currentTimeMillis();
            }
        }
    }

    private void dispose() {
        Log.info("Disposing...");
        Shader.dispose();
        SpriteMesh.dispose();
        Callbacks.glfwFreeCallbacks(window);

        GLFW.glfwDestroyWindow(window);
        this.window = -1;
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
}
