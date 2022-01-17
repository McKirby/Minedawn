package net.reindiegames.re2d.core.io;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface JsonNetIO extends JsonIO {
    static JsonObject loadObjectFromURL(String link) throws IOException {
        return PARSER.parse(IO.readURLContent(link)).getAsJsonObject();
    }

    default void loadFromUrl(String link) throws IOException {
        this.load(JsonNetIO.loadObjectFromURL(link));
    }
}
