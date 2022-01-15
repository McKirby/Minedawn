package net.reindiegames.re2d.core.io;

import com.google.gson.JsonObject;
import net.reindiegames.util.io.IO;
import net.reindiegames.util.io.JsonIO;

import java.io.IOException;

public interface JsonNetIO extends JsonIO {
    public static JsonObject loadObjectFromURL(String link) throws IOException {
        return PARSER.parse(IO.readURLContent(link)).getAsJsonObject();
    }

    public default void loadFromUrl(String link) throws IOException {
        this.load(net.reindiegames.util.io.JsonNetIO.loadObjectFromURL(link));
    }
}
