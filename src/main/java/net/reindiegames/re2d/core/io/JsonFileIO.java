package net.reindiegames.re2d.core.io;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface JsonFileIO extends JsonIO {
    public static boolean saveObjectToFile(JsonObject o, String folder, String file, boolean p) throws IOException {
        return IO.writeToFile(p ? FANCY_GSON.toJson(o) : GSON.toJson(o), folder, file);
    }

    public static JsonObject loadObjectFromFile(String folder, String file) throws IOException {
        return PARSER.parse(IO.readFileContent(folder, file)).getAsJsonObject();
    }

    public abstract JsonObject save();

    public default boolean saveToFile(String folder, String file, boolean p) throws IOException {
        return JsonFileIO.saveObjectToFile(this.save(), folder, file, p);
    }

    public default void loadFromFile(String folder, String file) throws IOException {
        this.load(JsonFileIO.loadObjectFromFile(folder, file));
    }
}
