package net.reindiegames.re2d.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.TileType;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.reindiegames.re2d.client.Mesh.*;
import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class ClientCoreBridge {
    protected static final int[] TILING_WIDTH = new int[2];
    protected static final Map<Integer, Map<Short, Mesh[]>> TILE_SPRITE_MAP = new HashMap<>();
    protected static final Map<Integer, TextureAtlas> TILE_ATLAS_MAP = new HashMap<>();

    protected static final int TILES_PER_CHUNK = CHUNK_SIZE * CHUNK_SIZE;
    protected static final FloatBuffer vertexBuffer;
    protected static final FloatBuffer textureBuffer;
    protected static final IntBuffer triangleBuffer;
    protected static final IntBuffer lineBuffer;
    static {
        vertexBuffer = MemoryUtil.memAllocFloat(TILES_PER_CHUNK * SPRITE_VERTICES.length);
        textureBuffer = MemoryUtil.memAllocFloat(TILES_PER_CHUNK * SPRITE_VERTICES.length);
        triangleBuffer = MemoryUtil.memAllocInt(TILES_PER_CHUNK * SPRITE_TRIANGLE_INDICES.length);
        lineBuffer = MemoryUtil.memAllocInt(TILES_PER_CHUNK * SPRITE_LINE_INDICES.length);
    }

    protected static final Map<Integer, Map<Integer, Mesh>> CHUNK_MESH_MAP = new HashMap<>();

    static {
        TILING_WIDTH[TileType.NO_TILING] = 1;
        TILING_WIDTH[TileType.COMPLETE_TILING] = 10;
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

                final Map<Short, Mesh[]> variantMap = new HashMap<>();
                for (short variant = 0; variant < spriteArray.size(); variant++) {
                    final JsonElement element = spriteArray.get(variant);

                    Mesh[] meshes;
                    int spriteIndex, column, row;
                    float[] texCoords;

                    if (element.isJsonPrimitive()) {
                        meshes = new Mesh[1];
                        spriteIndex = element.getAsInt();

                        column = spriteIndex % atlas.columns;
                        row = spriteIndex / atlas.columns;
                        texCoords = atlas.getTextureCoords(column, row);

                        meshes[0] = Mesh.create("sprite_tile_" + type.name + "_0", texCoords);
                    } else {
                        final JsonArray subSpriteArray = spriteArray.get(variant).getAsJsonArray();
                        meshes = new Mesh[subSpriteArray.size()];

                        for (int i = 0; i < subSpriteArray.size(); i++) {
                            spriteIndex = subSpriteArray.get(i).getAsInt();

                            column = spriteIndex % atlas.columns;
                            row = spriteIndex / atlas.columns;
                            texCoords = atlas.getTextureCoords(column, row);

                            meshes[i] = Mesh.create("sprite_tile_" + type.name + "_" + i, texCoords);
                        }
                    }
                    variantMap.put(variant, meshes);
                }
                TILE_SPRITE_MAP.put(type.id, variantMap);
            } catch (IllegalArgumentException e) {
                Log.error("Could not bridge to Tile '" + type.name + "'(" + e.getMessage() + ")!");
                return false;
            }
        }

        return true;
    }

    protected synchronized static Mesh generateTerrainMesh(Chunk chunk) {
        long start = System.currentTimeMillis();

        vertexBuffer.clear();
        textureBuffer.clear();
        triangleBuffer.clear();
        lineBuffer.clear();

        int offset = 0;
        int id;
        short variant;
        Mesh tileMesh;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                id = chunk.tiles[rx][ry];
                if (id == 0) continue;
                variant = chunk.variants[rx][ry];
                tileMesh = TILE_SPRITE_MAP.get(id).get(variant)[0];

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

        final int[] triangleIndices = new int[triangleBuffer.position()];
        triangleBuffer.flip();
        triangleBuffer.get(triangleIndices, 0, triangleIndices.length);

        final int[] lineIndices = new int[lineBuffer.position()];
        lineBuffer.flip();
        lineBuffer.get(lineIndices, 0, lineIndices.length);

        final Mesh mesh = new Mesh("chunk_" + chunk.cx + "_" + chunk.cy, v, t, triangleIndices, lineIndices);
        long delta = System.currentTimeMillis() - start;
        Log.debug("Generated Chunk-Mesh '" + mesh.name + "' in " + delta + "ms!");

        return mesh;
    }
}
