package net.reindiegames.re2d.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.Log;
import net.reindiegames.re2d.core.level.TileType;

import java.util.HashMap;
import java.util.Map;

public class ClientCoreBridge {
    protected static final int[] TILING_WIDTH = new int[2];
    protected static final Map<Integer, Map<Short, SpriteMesh[]>> TILE_SPRITE_MAP = new HashMap<>();
    protected static final Map<Integer, TextureAtlas> TILE_ATLAS_MAP = new HashMap<>();

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

                final Map<Short, SpriteMesh[]> variantMap = new HashMap<>();
                for (short variant = 0; variant < spriteArray.size(); variant++) {
                    final JsonElement element = spriteArray.get(variant);

                    SpriteMesh[] spriteMeshes;
                    int spriteIndex, column, row;
                    float[] texCoords;

                    if (element.isJsonPrimitive()) {
                        spriteMeshes = new SpriteMesh[1];
                        spriteIndex = element.getAsInt();

                        column = spriteIndex % atlas.columns;
                        row = spriteIndex / atlas.columns;
                        texCoords = atlas.getTextureCoords(column, row);

                        spriteMeshes[0] = SpriteMesh.create("sprite_tile_" + type.name + "_0", texCoords);
                    } else {
                        final JsonArray subSpriteArray = spriteArray.get(variant).getAsJsonArray();
                        spriteMeshes = new SpriteMesh[subSpriteArray.size()];

                        for (int i = 0; i < subSpriteArray.size(); i++) {
                            spriteIndex = subSpriteArray.get(i).getAsInt();

                            column = spriteIndex % atlas.columns;
                            row = spriteIndex / atlas.columns;
                            texCoords = atlas.getTextureCoords(column, row);

                            spriteMeshes[i] = SpriteMesh.create("sprite_tile_" + type.name + "_" + i, texCoords);
                        }
                    }
                    variantMap.put(variant, spriteMeshes);
                }
                TILE_SPRITE_MAP.put(type.id, variantMap);
            } catch (IllegalArgumentException e) {
                Log.error("Could not bridge to Tile '" + type.name + "'(" + e.getMessage() + ")!");
                return false;
            }
        }

        return true;
    }
}
