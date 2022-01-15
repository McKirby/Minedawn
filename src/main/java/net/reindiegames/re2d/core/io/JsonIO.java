package net.reindiegames.re2d.core.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.reindiegames.util.io.IO;

public interface JsonIO extends IO {
    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson FANCY_GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final JsonParser PARSER = new JsonParser();

    public abstract void load(JsonObject source);
}
