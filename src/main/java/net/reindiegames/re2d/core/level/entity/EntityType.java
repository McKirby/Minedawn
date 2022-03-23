package net.reindiegames.re2d.core.level.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class EntityType extends GameResource {
    public static final String RESOURCE_PATH = "core/level/entities/";

    public static EntityType PLAYER;
    public static EntityType ZOMBIE;
    public static EntityType ARROW;

    private static final Map<Integer, EntityType> ID_ENTITY_MAP = new HashMap<>();
    private static final Map<String, EntityType> RESOURCE_ENTITY_MAP = new HashMap<>();

    protected short[] states;
    protected float speed;

    private EntityType(String resource) {
        super(resource);
        ID_ENTITY_MAP.put(id, this);
        RESOURCE_ENTITY_MAP.put(resource, this);
    }

    private static void link() throws Exception {
        ReflectionUtil.setStatic(EntityType.class, "PLAYER", EntityType.getByResource("player.json"));
        ReflectionUtil.setStatic(EntityType.class, "ZOMBIE", EntityType.getByResource("zombie.json"));
        ReflectionUtil.setStatic(EntityType.class, "ARROW", EntityType.getByResource("arrow.json"));
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

    public short getActions() {
        return (short) states.length;
    }

    public short getActionStates(short action) {
        return states[action];
    }

    @Override
    public void load(JsonObject source) {
        final JsonArray actionArray = source.get("client").getAsJsonObject().get("sprites").getAsJsonArray();
        this.states = new short[actionArray.size()];
        for (int action = 0; action < states.length; action++) {
            JsonElement element = actionArray.get(action);
            if (element.isJsonArray()) {
                states[action] = (short) (element.getAsJsonArray().size() - 1);
            } else {
                states[action] = 1;
            }
        }

        final JsonObject attributeObjects = source.get("meta").getAsJsonObject().get("attributes").getAsJsonObject();
        this.speed = attributeObjects.get("speed").getAsFloat();
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resource;
    }
}
