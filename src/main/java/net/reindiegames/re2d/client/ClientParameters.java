package net.reindiegames.re2d.client;

import org.joml.Vector4f;

final class ClientParameters {
    protected static final int DEFAULT_HEIGHT = 800;
    protected static final float DEFAULT_ASPECT_RATIO = 4.0f / 3.0f;
    protected static final int DEFAULT_WIDTH = (int) (DEFAULT_HEIGHT * DEFAULT_ASPECT_RATIO);

    protected static final Vector4f clearColor = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);

    protected static final int MAX_TILE_PIXEL_SIZE = 256;
    protected static final int MIN_TILE_PIXEL_SIZE = 16;
    protected static final int DEFAULT_TILE_PIXEL_SIZE = MIN_TILE_PIXEL_SIZE;
    protected static int tileScale = DEFAULT_TILE_PIXEL_SIZE;
    protected static boolean tileScaleChanged = false;

    protected static int windowWidth;
    protected static int windowHeight;

    private ClientParameters() {
    }
}
