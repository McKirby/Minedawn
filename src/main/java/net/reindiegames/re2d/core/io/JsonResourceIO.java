package net.reindiegames.re2d.core.io;

import com.google.gson.JsonObject;
import net.reindiegames.util.io.IO;
import net.reindiegames.util.io.JsonIO;

public interface JsonResourceIO extends JsonIO {
    public static JsonObject loadObjectFromResource(String resource) {
        return PARSER.parse(IO.readFileContent(folderName, fileName)).getAsJsonObject();
    }
}
