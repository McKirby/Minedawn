package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.GameContext;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.CoordinateSystems;
import net.reindiegames.re2d.core.level.ResourceLevel;
import net.reindiegames.re2d.core.level.TileType;
import net.reindiegames.re2d.core.util.Disposer;
import net.reindiegames.re2d.core.util.Initializer;
import org.joml.Vector2f;
import org.joml.Vector2i;
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
    protected static float ctx;
    protected static float cty;
    protected static float speed = 1.0f;
    protected static ResourceLevel currentLevel;

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
        Input.addMouseAction(((pressed, x, y) -> {
            if (!pressed) return;

            final Vector2f levelPos = Input.getLevelPosition(x, y);
            final Vector2i chunkPos = CoordinateSystems.levelToChunk(levelPos);

            final Chunk chunk = currentLevel.getChunkBase().getChunk(chunkPos.x, chunkPos.y, true, true);
            final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
            chunk.tiles[relative.x][relative.y] = TileType.WATER.id;
            chunk.variants[relative.x][relative.y] = TileType.WATER.getDefaultVariant();
            chunk.changed = true;
        }));

        Log.info("Loading Assets..");
        if (!TextureAtlas.setup()) throw new IllegalStateException("Failed to load all Texture-Atlases!");

        Log.info("Setting up Render-Pipelines...");
        levelRenderPipeline = new LevelRenderPipeline();
        ctx = 0.0f;
        cty = 0.0f;

        Log.info("Bridging the Client to the Core...");
        if (!ClientCoreBridge.bridge()) throw new IllegalStateException("Could not Bridge between Core and Client!");

        Log.info("Loading Level...");
        currentLevel = ResourceLevel.TEST_LEVEL;
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
    }

    @Override
    protected void asyncTick(long totalTicks, float delta) {
        GLFW.glfwPollEvents();
        if (Input.MOVE_NORTH.isPressed()) cty += speed * delta;
        if (Input.MOVE_SOUTH.isPressed()) cty -= speed * delta;
        if (Input.MOVE_EAST.isPressed()) ctx += speed * delta;
        if (Input.MOVE_WEST.isPressed()) ctx -= speed * delta;

        if (Input.ZOOM_IN.isPressed()) {
            tileScale = Math.max(Math.min(tileScale - 1, MAX_TILE_PIXEL_SIZE), MIN_TILE_PIXEL_SIZE);
            tileScaleChanged = true;
        }

        if (Input.ZOOM_OUT.isPressed()) {
            tileScale = Math.max(Math.min(tileScale + 1, MAX_TILE_PIXEL_SIZE), MIN_TILE_PIXEL_SIZE);
            tileScaleChanged = true;
        }

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
