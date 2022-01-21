package net.reindiegames.re2d.core;

import com.google.gson.JsonObject;
import net.reindiegames.re2d.core.io.IO;
import net.reindiegames.re2d.core.io.JsonResourceIO;
import net.reindiegames.re2d.core.level.TileType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public abstract class GameResource implements JsonResourceIO {
    public final String resource;
    public final int id;
    public final String name;

    protected GameResource(String resource) {
        this.resource = resource;

        final JsonObject object = JsonResourceIO.loadObjectFromResource(this.getResourcePath());
        final JsonObject metaObject = object.get("meta").getAsJsonObject();
        this.id = metaObject.get("id").getAsInt();
        this.name = metaObject.get("name").getAsString();
    }

    public static boolean loadAll(Class<? extends GameResource> resourceImpl) {
        String path = null;
        try {
            final Field pathField = resourceImpl.getDeclaredField("RESOURCE_PATH");
            pathField.setAccessible(true);
            path = (String) pathField.get(null);
        } catch (ReflectiveOperationException e) {
            Log.error("The GameResource Path-Implementation '" + resourceImpl.getSimpleName() + "' is corrupt!");
            e.printStackTrace();
            return false;
        }

        try {
            Constructor constructor = resourceImpl.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);

            final List<String> resources = IO.listResources(path);
            for (String resource : resources) {
                final GameResource type = (GameResource) constructor.newInstance(resource);
                type.loadFromResource();
            }
        } catch (ReflectiveOperationException e) {
            Log.error("The GameResource Load-Implementation '" + resourceImpl.getSimpleName() + "' is corrupt!");
            e.printStackTrace();
            return false;
        }

        try {
            final Method linkMethod = resourceImpl.getDeclaredMethod("link");
            linkMethod.setAccessible(true);
            linkMethod.invoke(null);
        } catch (ReflectiveOperationException e) {
            Log.error("The GameResource Link-Implementation '" + resourceImpl.getSimpleName() + "' is corrupt!");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TileType) return ((TileType) o).id == this.id;
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{Resource = '" + resource + "', ID = " + id + "}";
    }
}
