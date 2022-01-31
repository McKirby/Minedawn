package net.reindiegames.re2d.core.game;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class MaterialType extends GameResource {
    public static final String RESOURCE_PATH = "core/game/material/";

    public static MaterialType COPPER;
    public static MaterialType IRON;
    public static MaterialType GOLD;
    public static MaterialType SILVER;

    private static final Map<Integer, MaterialType> ID_MATERIAL_MAP = new HashMap<>();
    private static final Map<String, MaterialType> RESOURCE_MATERIAL_MAP = new HashMap<>();

    private MaterialType(String resource) {
        super(resource);
        ID_MATERIAL_MAP.put(id, this);
        RESOURCE_MATERIAL_MAP.put(resource, this);
    }

    private static void link() throws Exception {
        ReflectionUtil.setStatic(MaterialType.class, "COPPER", MaterialType.getByResource("copper.json"));
        ReflectionUtil.setStatic(MaterialType.class, "IRON", MaterialType.getByResource("iron.json"));
        ReflectionUtil.setStatic(MaterialType.class, "GOLD", MaterialType.getByResource("gold.json"));
        ReflectionUtil.setStatic(MaterialType.class, "SILVER", MaterialType.getByResource("silver.json"));
    }

    public static MaterialType getById(int id) {
        return ID_MATERIAL_MAP.getOrDefault(id, null);
    }

    public static MaterialType getByResource(String resource) {
        return RESOURCE_MATERIAL_MAP.getOrDefault(resource, null);
    }

    public static MaterialType[] getTypes() {
        return ID_MATERIAL_MAP.values().toArray(new MaterialType[ID_MATERIAL_MAP.size()]);
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resource;
    }
}
