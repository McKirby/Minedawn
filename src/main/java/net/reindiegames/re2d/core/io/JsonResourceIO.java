package net.reindiegames.re2d.core.io;

import com.google.gson.JsonObject;

public interface JsonResourceIO extends JsonIO, Resource {
    public static JsonObject loadObjectFromResource(String resource) throws IllegalArgumentException {
        return PARSER.parse(IO.readResourceContent(resource)).getAsJsonObject();
    }

    public default void loadFromResource() throws IllegalArgumentException {
        this.load(JsonResourceIO.loadObjectFromResource(this.getResourcePath()));
    }
}
