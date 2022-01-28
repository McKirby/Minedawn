package net.reindiegames.re2d.core.game.tasks;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.GameResource;

import java.util.HashMap;
import java.util.Map;

public class Task extends GameResource {
    public static final String RESOURCE_PATH = "core/tasks/";

    private static final Map<Integer, Task> ID_TASK_MAP = new HashMap<>();
    private static final Map<String, Task> RESOURCE_TASK_MAP = new HashMap<>();

    protected Task(String resource) {
        super(resource);
        ID_TASK_MAP.put(id, this);
        RESOURCE_TASK_MAP.put(resource, this);
    }

    private static void link() throws Exception {
    }

    public static Task getById(int id) {
        return ID_TASK_MAP.getOrDefault(id, null);
    }

    public static Task getByResource(String resource) {
        return RESOURCE_TASK_MAP.getOrDefault(resource, null);
    }

    public static Task[] getTypes() {
        return ID_TASK_MAP.values().toArray(new Task[ID_TASK_MAP.size()]);
    }

    @Override
    public void load(JsonObject source) {
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH + resource;
    }
}
