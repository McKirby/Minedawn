package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.GameContext;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.util.Disposer;
import net.reindiegames.re2d.core.util.Initializer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static net.reindiegames.re2d.client.ClientParameters.DEFAULT_HEIGHT;
import static net.reindiegames.re2d.client.ClientParameters.DEFAULT_WIDTH;
import static net.reindiegames.re2d.core.CoreParameters.TITLE;
import static net.reindiegames.re2d.core.CoreParameters.debug;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ClientContext extends GameContext {
    protected static ClientContext runningContext = null;

    protected static LevelRenderPipeline levelRenderPipeline;
    protected static long window;
    protected static float ctx;
    protected static float cty;
    protected static Level currentLevel;

    private ClientContext() {
    }

    @Initializer
    private static final void initialize() {
        if (runningContext != null) throw new IllegalStateException("There is already a ClientContext Running!");

        Log.info("Bridging the Client to the Core...");
        ClientCoreBridge.bridge();

        Log.info("Creating OpenGL Context...");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW!");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, TITLE, NULL, NULL);
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
        GL.createCapabilities();

        Log.info("Linking Inputs...");
        if (!Input.setup(window)) throw new IllegalStateException("Could not link Input!");

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

        Log.info("Loading Assets..");
        if (!TextureAtlas.setup()) throw new IllegalStateException("Failed to load all Texture-Atlases!");

        Log.info("Setting up Render-Pipelines...");
        levelRenderPipeline = new LevelRenderPipeline();
        ctx = 0.0f;
        cty = 0.0f;

        Log.info("Loading Level...");
        currentLevel = new Level();
    }

    @Disposer
    private static final void dispose() {
        Log.info("Disposing Assets...");
        Shader.dispose();
        TextureAtlas.dispose();
        SpriteMesh.dispose();

        Log.info("Disposing the OpenGL-Context...");
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    @Override
    protected void syncTick(long totalTicks, float delta) {
    }

    @Override
    protected void asyncTick(long totalTicks, float delta) {
        GLFW.glfwPollEvents();
        levelRenderPipeline.render(currentLevel, window, ctx, cty, totalTicks);
        GLFW.glfwSwapBuffers(window);
    }

    @Override
    protected boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    @Override
    protected String debugInfo(int syncTicks, int asyncTicks) {
        return "FPS: " + asyncTicks + ", TPS: " + syncTicks;
    }
}
