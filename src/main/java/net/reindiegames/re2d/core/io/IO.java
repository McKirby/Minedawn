package net.reindiegames.re2d.core.io;

import java.io.*;
import java.net.URL;

public interface IO {
    public static String readStreamContent(Reader in) throws IOException {
        final BufferedReader reader = new BufferedReader(in);
        final StringBuilder buffer = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    public static String readFileContent(String folderName, String fileName) throws IOException {
        final File file = new File(folderName, fileName);
        if (!file.exists()) return "";

        FileReader in = null;
        try {
            in = new FileReader(file);
            return net.reindiegames.util.io.IO.readStreamContent(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String readResourceContent(String resource) throws IOException {
        final InputStream in = net.reindiegames.util.io.IO.class.getClassLoader().getResourceAsStream(resource);
        final InputStreamReader reader = new InputStreamReader(in);
        return net.reindiegames.util.io.IO.readStreamContent(reader);
    }

    public static boolean writeToFile(String value, String folderName, String fileName) throws IOException {
        final File folder = new File(folderName);
        if (!folder.exists() && !folder.mkdirs()) return false;

        final File file = new File(folder, fileName);
        if (!file.exists() && !file.createNewFile()) return false;

        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(value);
        writer.flush();
        writer.close();
        return true;
    }

    public static String readURLContent(String link) throws IOException {
        URL url = new URL(link);
        InputStreamReader in = null;

        try {
            in = new InputStreamReader(url.openStream());
            return net.reindiegames.util.io.IO.readStreamContent(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
