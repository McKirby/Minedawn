package net.reindiegames.re2d.core.io;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface JsonFileIO extends JsonIO {
    public static boolean saveObjectToFile(JsonObject o, String folder, String file, boolean p) throws IOException {
        return IO.writeToFile(p ? FANCY_GSON.toJson(o) : GSON.toJson(o), folder, file);
    }

    public static JsonObject loadObjectFromFile(String folderName, String fileName) throws IOException {
        return PARSER.parse(IO.readFileContent(folderName, fileName)).getAsJsonObject();
    }

    public abstract JsonObject save();

    public default boolean saveToFile(String folderName, String fileName, boolean pretty) throws IOException {
        return JsonFileIO.saveObjectToFile(this.save(), folderName, fileName, pretty);
    }

    public default void loadFromFile(String folderName, String fileName) throws IOException {
        this.load(JsonFileIO.loadObjectFromFile(folderName, fileName));
    }
}
