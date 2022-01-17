package net.reindiegames.re2d.core.io;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface JsonFileIO extends JsonIO {
    static boolean saveObjectToFile(JsonObject o, String folder, String file, boolean p) throws IOException {
        return IO.writeToFile(p ? FANCY_GSON.toJson(o) : GSON.toJson(o), folder, file);
    }

    static JsonObject loadObjectFromFile(String folder, String file) throws IOException {
        return PARSER.parse(IO.readFileContent(folder, file)).getAsJsonObject();
    }

    JsonObject save();

    default boolean saveToFile(String folder, String file, boolean p) throws IOException {
        return JsonFileIO.saveObjectToFile(this.save(), folder, file, p);
    }

    default void loadFromFile(String folder, String file) throws IOException {
        this.load(JsonFileIO.loadObjectFromFile(folder, file));
    }
}
