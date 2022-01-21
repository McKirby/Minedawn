package net.reindiegames.re2d.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.Tile;
import net.reindiegames.re2d.core.level.TileType;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.reindiegames.re2d.client.Mesh.*;
import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class ClientCoreBridge {
    protected static final Map<Integer, Map<Short, Mesh[]>> TILE_SPRITE_MAP = new HashMap<>();
    protected static final Map<Integer, Map<Short, AnimationParameters>> TILE_ANIMATION_MAP = new HashMap<>();
    protected static final Map<Integer, TextureAtlas> TILE_ATLAS_MAP = new HashMap<>();

    protected static final int TILES_PER_CHUNK = CHUNK_SIZE * CHUNK_SIZE;
    protected static final FloatBuffer vertexBuffer;
    protected static final FloatBuffer textureBuffer;
    protected static final IntBuffer triangleBuffer;
    protected static final IntBuffer lineBuffer;
    protected static final Map<Integer, Map<Integer, Mesh[]>> CHUNK_MESH_MAP = new HashMap<>();

    static {
        vertexBuffer = MemoryUtil.memAllocFloat(TILES_PER_CHUNK * SPRITE_VERTICES.length);
        textureBuffer = MemoryUtil.memAllocFloat(TILES_PER_CHUNK * SPRITE_VERTICES.length);
        triangleBuffer = MemoryUtil.memAllocInt(TILES_PER_CHUNK * SPRITE_TRIANGLE_INDICES.length);
        lineBuffer = MemoryUtil.memAllocInt(TILES_PER_CHUNK * SPRITE_LINE_INDICES.length);
    }

    public static boolean bridge() {
        if (!ClientCoreBridge.bridgeTileSprites()) return false;
        return true;
    }

    private static boolean bridgeTileSprites() {
        final TileType[] types = TileType.getTypes();
        for (TileType type : types) {
            try {
                final JsonObject clientObject = type.loadResourceObject().get("client").getAsJsonObject();
                final TextureAtlas atlas = TextureAtlas.valueOf(clientObject.get("atlas").getAsString());
                TILE_ATLAS_MAP.put(type.id, atlas);

                final JsonArray spriteArray = clientObject.get("sprites").getAsJsonArray();
                if (spriteArray.size() != TileType.TILING_VARIANTS[type.getTiling()]) {
                    throw new IllegalArgumentException("The Sprite-Count does not match the Tiling!");
                }

                final Map<Short, Mesh[]> variantMeshMap = new HashMap<>();
                final Map<Short, AnimationParameters> variantAnimationTicksMap = new HashMap<>();
                for (short variant = 0; variant < spriteArray.size(); variant++) {
                    final JsonElement element = spriteArray.get(variant);

                    Mesh[] meshes;
                    int animationFrames, animationTicks;
                    int spriteIndex, column, row;
                    float[] texCoords;

                    if (element.isJsonPrimitive()) {
                        meshes = new Mesh[1];
                        animationFrames = 1;
                        animationTicks = 1;

                        spriteIndex = element.getAsInt();

                        column = spriteIndex % atlas.columns;
                        row = spriteIndex / atlas.columns;
                        texCoords = atlas.getTextureCoords(column, row);

                        meshes[0] = Mesh.create("sprite_tile_" + type.name + "_0", texCoords);
                    } else {
                        final JsonArray subSpriteArray = spriteArray.get(variant).getAsJsonArray();
                        meshes = new Mesh[subSpriteArray.size() - 1];

                        animationTicks = (int) (CoreParameters.TICK_RATE * subSpriteArray.get(0).getAsFloat());
                        animationFrames = meshes.length;

                        for (int i = 1; i < subSpriteArray.size(); i++) {
                            spriteIndex = subSpriteArray.get(i).getAsInt();

                            column = spriteIndex % atlas.columns;
                            row = spriteIndex / atlas.columns;
                            texCoords = atlas.getTextureCoords(column, row);

                            meshes[i - 1] = Mesh.create("sprite_tile_" + type.name + "_" + i, texCoords);
                        }
                    }
                    variantMeshMap.put(variant, meshes);
                    variantAnimationTicksMap.put(variant, new AnimationParameters(animationFrames, animationTicks));
                }
                TILE_SPRITE_MAP.put(type.id, variantMeshMap);
                TILE_ANIMATION_MAP.put(type.id, variantAnimationTicksMap);
            } catch (IllegalArgumentException e) {
                Log.error("Could not bridge to Tile '" + type.name + "'(" + e.getMessage() + ")!");
                return false;
            }
        }

        return true;
    }

    protected synchronized static Mesh[] generateTerrainMesh(Chunk c) {
        long start = System.currentTimeMillis();
        int maxAnimationDuration = 1;
        int animationDuration;

        Tile tile;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                tile = c.tiles[rx][ry];
                if (tile == null) continue;

                animationDuration = TILE_ANIMATION_MAP.get(tile.type.id).get(tile.variant).duration;
                if (animationDuration > maxAnimationDuration) {
                    maxAnimationDuration = animationDuration;
                }
            }
        }

        final Mesh[] meshes = new Mesh[maxAnimationDuration];
        for (int i = 0; i < meshes.length; i++) {
            meshes[i] = ClientCoreBridge.generateTickTerrainMesh(i, c);
        }

        final long delta = System.currentTimeMillis() - start;
        Log.debug("Generated Chunk-Mesh (" + meshes.length + ", " + c.cx + ", " + c.cy + ") in " + delta + "ms!");

        return meshes;
    }

    private static Mesh generateTickTerrainMesh(int tick, Chunk chunk) {
        vertexBuffer.clear();
        textureBuffer.clear();
        triangleBuffer.clear();
        lineBuffer.clear();

        int offset = 0;

        Tile tile;
        AnimationParameters p;
        Mesh tileMesh;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                tile = chunk.tiles[rx][ry];
                if (tile == null) continue;

                p = TILE_ANIMATION_MAP.get(tile.type.id).get(tile.variant);
                tileMesh = TILE_SPRITE_MAP.get(tile.type.id).get(tile.variant)[(tick / p.animationTicks) % p.frames];

                for (int i = 0; i < tileMesh.vertices.length; i += 2) {
                    vertexBuffer.put((tileMesh.vertices[i + 0] + rx) / CHUNK_SIZE);
                    vertexBuffer.put((tileMesh.vertices[i + 1] + ry) / CHUNK_SIZE);
                }
                textureBuffer.put(tileMesh.textureCoordinates);

                for (int i = 0; i < tileMesh.triangleIndices.length; i++) {
                    triangleBuffer.put(tileMesh.triangleIndices[i] + offset);
                }

                for (int i = 0; i < tileMesh.lineIndices.length; i++) {
                    lineBuffer.put(tileMesh.lineIndices[i] + offset);
                }
                offset += 4;
            }
        }

        final float[] v = new float[vertexBuffer.position()];
        vertexBuffer.flip();
        vertexBuffer.get(v, 0, v.length);

        final float[] t = new float[textureBuffer.position()];
        textureBuffer.flip();
        textureBuffer.get(t, 0, t.length);

        final int[] triIndices = new int[triangleBuffer.position()];
        triangleBuffer.flip();
        triangleBuffer.get(triIndices, 0, triIndices.length);

        final int[] liIndices = new int[lineBuffer.position()];
        lineBuffer.flip();
        lineBuffer.get(liIndices, 0, liIndices.length);

        return new Mesh("chunk_" + chunk.cx + "_" + chunk.cy + "_" + tick, v, t, triIndices, liIndices);
    }

    static class AnimationParameters {
        protected final int frames;
        protected final int animationTicks;
        protected final int duration;

        protected AnimationParameters(int frames, int animationTicks) {
            this.frames = frames;
            this.animationTicks = animationTicks;
            this.duration = frames * animationTicks;
        }
    }
}
