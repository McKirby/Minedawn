package net.reindiegames.re2d.client;

import de.matthiasmann.twl.utils.PNGDecoder;
import net.reindiegames.re2d.core.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

enum TextureAtlas {
    TERRAIN_01("client/sprites/terrain.png", 32, 32);

    protected static final int SPRITE_PIXEL_SIZE = 32;

    protected final String resource;
    protected final int columns;
    protected final int rows;

    protected int textureId;

    TextureAtlas(String resource, int columns, int rows) {
        this.resource = resource;
        this.columns = columns;
        this.rows = rows;
    }

    protected static boolean setup() {
        for (TextureAtlas atlas : TextureAtlas.values()) {
            InputStream in = null;
            PNGDecoder decoder = null;
            ByteBuffer buffer = null;
            try {
                in = TextureAtlas.class.getClassLoader().getResourceAsStream(atlas.resource);
                decoder = new PNGDecoder(in);

                buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(buffer, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
                buffer.flip();
            } catch (IOException e) {
                Log.error("Could not load Atlas '" + atlas.resource + "' (" + e.getMessage() + ")!");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            atlas.textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.textureId);

            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(),
                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer
            );
        }
        return true;
    }

    protected static void dispose() {
        for (TextureAtlas atlas : TextureAtlas.values()) {
            GL11.glDeleteTextures(atlas.textureId);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    protected float[] getTextureCoords(int column, int row) {
        float xStart = ((float) column) / ((float) columns);
        float yStart = ((float) row) / ((float) rows);
        float xStep = 1.0f / ((float) columns);
        float yStep = 1.0f / ((float) rows);

        float xe = (1.0f / (columns * SPRITE_PIXEL_SIZE)) * 0.1f;
        float ye = (1.0f / (rows * SPRITE_PIXEL_SIZE)) * 0.1f;

        return new float[] {
                xStart + 0 * xStep + xe, yStart + 0 * yStep + ye,
                xStart + 1 * xStep - xe, yStart + 0 * yStep + ye,
                xStart + 0 * xStep + xe, yStart + 1 * yStep - ye,
                xStart + 1 * xStep - xe, yStart + 1 * yStep - ye,
        };
    }

    protected void bind(int textureBank) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureBank);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }
}
