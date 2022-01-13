package net.reindiegames.re2d.client.input;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public enum Input {
    MOVE_NORTH(GLFW.GLFW_KEY_W),
    MOVE_WEST(GLFW.GLFW_KEY_A),
    MOVE_EAST(GLFW.GLFW_KEY_D),
    MOVE_SOUTH(GLFW.GLFW_KEY_S),
    EXIT_GAME(GLFW.GLFW_KEY_ESCAPE),
    DEBUG(GLFW.GLFW_KEY_LEFT_SHIFT);

    private static final Map<Integer, Input> keyInputMap = new HashMap<>();
    private static long window;

    private final int keyCode;
    private boolean pressed;
    private KeyAction action;

    private Input(int keyCode) {
        this.keyCode = keyCode;
    }

    public static void create(long w) {
        for (Input input : Input.values()) {
            keyInputMap.put(input.keyCode, input);
        }

        window = w;
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_REPEAT) return;

            final Input input = keyInputMap.getOrDefault(key, null);
            if (input == null) return;

            input.pressed = action == GLFW.GLFW_PRESS;
            if (input.action != null) {
                input.action.onKeyAction(input.pressed);
            }
        });
    }

    public void setAction(KeyAction action) {
        this.action = action;
    }

    public boolean isPressed() {
        return pressed;
    }
}
