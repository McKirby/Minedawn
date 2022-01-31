package net.reindiegames.re2d.core.level.entity;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class EntityType extends GameResource {
    public static final String RESOURCE_PATH = "core/level/entities/";

    public static EntityType PLAYER;

    private static final Map<Integer, EntityType> ID_ENTITY_MAP = new HashMap<>();
    private static final Map<String, EntityType> RESOURCE_ENTITY_MAP = new HashMap<>();

    private EntityType(String resource) {
        super(resource);
        ID_ENTITY_MAP.put(id, this);
        RESOURCE_ENTITY_MAP.put(resource, this);
    }

    private static void link() throws Exception {
        ReflectionUtil.setStatic(EntityType.class, "PLAYER", EntityType.getByResource("player.json"));
    }

    public static EntityType getById(int id) {
        return ID_ENTITY_MAP.getOrDefault(id, null);
    }

    public static EntityType getByResource(String resource) {
        return RESOURCE_ENTITY_MAP.getOrDefault(resource, null);
    }

    public static EntityType[] getTypes() {
        return ID_ENTITY_MAP.values().toArray(new EntityType[ID_ENTITY_MAP.size()]);
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resource;
    }
}
