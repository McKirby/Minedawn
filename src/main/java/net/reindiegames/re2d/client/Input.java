package net.reindiegames.re2d.client;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.reindiegames.re2d.client.ClientParameters.*;

enum Input {
    MOVE_NORTH(GLFW.GLFW_KEY_W),
    MOVE_WEST(GLFW.GLFW_KEY_A),
    MOVE_EAST(GLFW.GLFW_KEY_D),
    MOVE_SOUTH(GLFW.GLFW_KEY_S),
    ZOOM_OUT(GLFW.GLFW_KEY_Q),
    ZOOM_IN(GLFW.GLFW_KEY_E),
    SHOOT(GLFW.GLFW_KEY_SPACE),
    EXIT_GAME(GLFW.GLFW_KEY_ESCAPE),
    DEBUG(GLFW.GLFW_KEY_TAB);

    private static final Map<Integer, Input> keyInputMap = new HashMap<>();

    private static final List<MouseAction> mouseActions = new ArrayList<>();

    private static long window;
    private static Vector2d cursorPos;

    private final int keyCode;
    private boolean pressed;
    private KeyAction action;

    Input(int keyCode) {
        this.keyCode = keyCode;
    }

    protected static boolean setup(long w) {
        for (Input input : Input.values()) {
            keyInputMap.put(input.keyCode, input);
        }

        window = w;
        cursorPos = new Vector2d();

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_REPEAT) return;

            final Input input = keyInputMap.getOrDefault(key, null);
            if (input == null) return;

            input.pressed = action == GLFW.GLFW_PRESS;
            if (input.action != null) {
                input.action.onKeyAction(input.pressed);
            }
        });

        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            synchronized (mouseActions) {
                float x = (float) cursorPos.x;
                float y = (float) cursorPos.y;

                for (MouseAction mouseAction : mouseActions) {
                    mouseAction.onMouseAction(button, action == GLFW.GLFW_PRESS, x, y);
                }
            }
        });

        GLFW.glfwSetCursorPosCallback(window, (window, x, y) -> {
            cursorPos.x = x;
            cursorPos.y = y;
        });
        return true;
    }

    protected static Vector2f getCursor() {
        return new Vector2f((float) cursorPos.x, (float) cursorPos.y);
    }

    protected static void addMouseAction(MouseAction action) {
        synchronized (mouseActions) {
            mouseActions.add(action);
        }
    }

    protected static Vector2f getCursorLevelPosition() {
        float xDiff = (float) (cursorPos.x - (windowWidth / 2.0f));
        float yDiff = (float) -(cursorPos.y - (windowHeight / 2.0f));

        final Vector2f playerCenter = ClientContext.player.getCenter();
        final Vector2f pos = new Vector2f(0.0f, 0.0f);
        pos.x = playerCenter.x + (xDiff / tileScale);
        pos.y = playerCenter.y + (yDiff / tileScale);

        return pos;
    }

    protected void setAction(KeyAction action) {
        this.action = action;
    }

    protected boolean isPressed() {
        return pressed;
    }

    interface KeyAction {
        void onKeyAction(boolean pressed);
    }

    interface MouseAction {
        void onMouseAction(int button, boolean pressed, float x, float y);
    }
}
