package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.GameContext;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.level.DungeonChunkGenerator;
import net.reindiegames.re2d.core.level.GeneratedLevel;
import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.TileType;
import net.reindiegames.re2d.core.util.Disposer;
import net.reindiegames.re2d.core.util.Initializer;
import org.joml.Vector2f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static net.reindiegames.re2d.client.ClientParameters.*;
import static net.reindiegames.re2d.core.CoreParameters.TITLE;
import static net.reindiegames.re2d.core.CoreParameters.debug;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ClientContext extends GameContext {
    protected static ClientContext runningContext = null;

    protected static LevelRenderPipeline levelRenderPipeline;
    protected static long window = -1;
    protected static float speed = 1.0f;

    protected static Level currentLevel;
    protected static EntityClientPlayer player;

    private ClientContext() {
    }

    @Initializer
    private static final void initialize() {
        if (runningContext != null) throw new IllegalStateException("There is already a ClientContext Running!");

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
            ClientParameters.windowWidth = pWidth.get();
            ClientParameters.windowHeight = pHeight.get();

            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            int centerX = (vidmode.width() - ClientParameters.windowWidth) / 2;
            int centerY = (vidmode.height() - ClientParameters.windowHeight) / 2;
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
        Input.addMouseAction(((button, pressed, x, y) -> {
            if (!pressed) return;
            final Vector2f goal = Input.getLevelPosition(x, y);

            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                player.navigator.navigate(goal);
            } else {
                player.level.setTileType(goal, TileType.GRASS);
            }

        }));

        Log.info("Loading Assets..");
        if (!TextureAtlas.setup()) throw new IllegalStateException("Failed to load all Texture-Atlases!");

        Log.info("Setting up Render-Pipelines...");
        levelRenderPipeline = new LevelRenderPipeline();

        Log.info("Bridging the Client to the Core...");
        if (!ClientCoreBridge.bridge()) throw new IllegalStateException("Could not Bridge between Core and Client!");

        Log.info("Loading Level...");
        int scale = 3;
        currentLevel = new GeneratedLevel(1337, new DungeonChunkGenerator(10 * scale, 10 * scale, scale));
        player = currentLevel.spawn(EntityClientPlayer.class, currentLevel.getSpawn());
    }

    @Disposer
    private static final void dispose() {
        if (window != -1) {
            Log.info("Disposing Assets...");
            Shader.dispose();
            TextureAtlas.dispose();
            Mesh.dispose();

            Log.info("Disposing the OpenGL-Context...");
            Callbacks.glfwFreeCallbacks(window);
            GLFW.glfwDestroyWindow(window);
            GLFW.glfwTerminate();
            GLFW.glfwSetErrorCallback(null).free();
        }
    }

    @Override
    protected void syncTick(long totalTicks, float delta) {
        super.syncTick(totalTicks, delta);
        currentLevel.syncTick(totalTicks, delta);
    }

    @Override
    protected void asyncTick(long totalTicks, float delta) {
        GLFW.glfwPollEvents();

        currentLevel.asyncTick(totalTicks, delta);
        levelRenderPipeline.render(currentLevel, window, player.getCenter(), totalTicks);
        GLFW.glfwSwapBuffers(window);
        tileScaleChanged = false;
    }

    @Override
    protected boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    @Override
    protected String debugInfo(int syncTicks, int asyncTicks) {
        return GameContext.DAY_NIGHT_CIRCLE.getTimeString() + ", FPS: " + asyncTicks + ", TPS: " + syncTicks;
    }
}
