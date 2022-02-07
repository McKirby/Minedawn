package net.reindiegames.re2d.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.Tile;
import net.reindiegames.re2d.core.level.TileType;
import net.reindiegames.re2d.core.level.entity.EntityType;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.reindiegames.re2d.client.Mesh.*;
import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

class ClientCoreBridge {
    protected static final Map<Integer, RenderCompound> TILE_COMPOUND_MAP = new HashMap<>();
    protected static final Map<Integer, RenderCompound> ENTITY_COMPOUND_MAP = new HashMap<>();

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
        if (!ClientCoreBridge.bridgeTiles()) return false;
        if (!ClientCoreBridge.bridgeEntities()) return false;
        return true;
    }

    private static void loadCompound(
            GameResource resource, JsonObject source, String prefix, Map<Integer, RenderCompound> target
    ) throws IllegalArgumentException {
        final RenderCompound compound = new RenderCompound(resource);
        compound.atlas = TextureAtlas.valueOf(source.get("atlas").getAsString());

        final JsonArray spriteArray = source.get("sprites").getAsJsonArray();
        compound.sprites = new Mesh[spriteArray.size()][];
        compound.animation = new RenderCompound.AnimationParameters[spriteArray.size()];

        for (short variant = 0; variant < spriteArray.size(); variant++) {
            final JsonElement element = spriteArray.get(variant);

            int animationFrames, animationTicks;
            int spriteIndex, column, row;
            float[] texCoords;

            if (element.isJsonPrimitive()) {
                compound.sprites[variant] = new Mesh[1];
                animationFrames = 1;
                animationTicks = 1;

                spriteIndex = element.getAsInt();

                column = spriteIndex % compound.atlas.columns;
                row = spriteIndex / compound.atlas.columns;
                texCoords = compound.atlas.getTextureCoords(column, row);

                compound.sprites[variant][0] = Mesh.create(prefix + resource.name + "_0", texCoords);
            } else {
                final JsonArray subSpriteArray = spriteArray.get(variant).getAsJsonArray();
                compound.sprites[variant] = new Mesh[subSpriteArray.size() - 1];

                animationTicks = (int) (CoreParameters.TICK_RATE * subSpriteArray.get(0).getAsFloat());
                animationFrames = compound.sprites[variant].length;

                for (int i = 1; i < subSpriteArray.size(); i++) {
                    spriteIndex = subSpriteArray.get(i).getAsInt();

                    column = spriteIndex % compound.atlas.columns;
                    row = spriteIndex / compound.atlas.columns;
                    texCoords = compound.atlas.getTextureCoords(column, row);

                    compound.sprites[variant][i - 1] = Mesh.create(resource + resource.name + "_" + i, texCoords);
                }
            }
            compound.animation[variant] = new RenderCompound.AnimationParameters(animationFrames, animationTicks);
        }

        target.put(resource.id, compound);
    }

    private static boolean bridgeTiles() {
        final TileType[] types = TileType.getTypes();
        for (TileType type : types) {
            try {
                final JsonObject clientObject = type.loadResourceObject().get("client").getAsJsonObject();
                ClientCoreBridge.loadCompound(type, clientObject, "sprite_tile_", TILE_COMPOUND_MAP);
            } catch (IllegalArgumentException e) {
                Log.error("Could not bridge to Tile '" + type.name + "'(" + e.getMessage() + ")!");
                return false;
            }
        }

        return true;
    }

    private static boolean bridgeEntities() {
        final EntityType[] types = EntityType.getTypes();
        for (EntityType type : types) {
            try {
                final JsonObject clientObject = type.loadResourceObject().get("client").getAsJsonObject();
                ClientCoreBridge.loadCompound(type, clientObject, "entity_", ENTITY_COMPOUND_MAP);
            } catch (IllegalArgumentException e) {
                Log.error("Could not bridge to Tile '" + type.name + "'(" + e.getMessage() + ")!");
                return false;
            }
        }

        return true;
    }

    protected synchronized static Mesh[] generateTerrainMesh(Chunk c) {
        final long start = System.currentTimeMillis();
        int maxAnimationDuration = 1;
        int animationDuration;

        Tile tile;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                tile = c.tiles[rx][ry];
                if (tile == null) continue;

                animationDuration = TILE_COMPOUND_MAP.get(tile.type.id).animation[tile.variant].duration;
                if (animationDuration > maxAnimationDuration) {
                    maxAnimationDuration = animationDuration;
                }
            }
        }

        final Mesh[] meshes = new Mesh[maxAnimationDuration];
        for (int i = 0; i < meshes.length; i++) {
            meshes[i] = ClientCoreBridge.generateTickTerrainMesh(i, c);
        }

        long delta = System.currentTimeMillis() - start;
        Log.debug("Generated ChunkMesh in " + delta + "ms!");

        return meshes;
    }

    private static Mesh generateTickTerrainMesh(int tick, Chunk chunk) {
        vertexBuffer.clear();
        textureBuffer.clear();
        triangleBuffer.clear();
        lineBuffer.clear();

        int offset = 0;

        RenderCompound compound;
        Tile tile;
        RenderCompound.AnimationParameters p;
        Mesh tileMesh;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                tile = chunk.tiles[rx][ry];
                if (tile == null) continue;

                compound = TILE_COMPOUND_MAP.get(tile.type.id);
                p = compound.animation[tile.variant];
                tileMesh = compound.sprites[tile.variant][(tick / p.ticks) % p.frames];

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

}
