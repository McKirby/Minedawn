package net.reindiegames.re2d.core.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public interface JsonIO extends IO {
    Gson GSON = new GsonBuilder().create();
    Gson FANCY_GSON = new GsonBuilder().setPrettyPrinting().create();
    JsonParser PARSER = new JsonParser();

    void load(JsonObject source);
}
