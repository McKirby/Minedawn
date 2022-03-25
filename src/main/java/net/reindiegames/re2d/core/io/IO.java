package net.reindiegames.re2d.core.io;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public interface IO {
    public static String readStreamContent(BufferedReader reader) throws IOException {
        final StringBuilder buffer = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }

    public static String readFileContent(String folder, String file) throws IOException {
        final File fi = new File(folder, file);
        if (!fi.exists()) {
            throw new FileNotFoundException("The File '" + file + "' in '" + folder + "' does not exists!");
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(fi);
            return IO.readStreamContent(new BufferedReader(new InputStreamReader(in)));
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static boolean writeToFile(String value, String folderName, String fileName) throws IOException {
        final File folder = new File(folderName);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Can not create Folder '" + folderName + "'!");
        }

        final File file = new File(folder, fileName);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Can not write to File '" + fileName + "' in '" + folderName + "'!");
        }

        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(value);
        writer.flush();
        writer.close();
        return true;
    }

    public static String readResourceContent(String resource) throws IllegalArgumentException {
        final InputStream in = IO.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) throw new IllegalArgumentException("The Resource '" + resource + "' does not exist!");

        try {
            return IO.readStreamContent(new BufferedReader(new InputStreamReader(in)));
        } catch (IOException e) {
            throw new IllegalArgumentException("The Resource '" + resource + "' does not exist!");
        }
    }

    public static List<String> listResources(String folder) throws IllegalArgumentException {
        final InputStream in = IO.class.getClassLoader().getResourceAsStream(folder);
        if (in == null) throw new IllegalArgumentException("The ResourceFolder '" + folder + "' does not exists!");

        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        final List<String> resources = new ArrayList<String>();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(".")) continue;
                resources.add(line);
            }
        } catch (IOException e) {

        }

        return resources;
    }

    public static String readURLContent(String link) throws IOException {
        URL url = new URL(link);
        InputStream in = null;

        try {
            in = url.openStream();
            return IO.readStreamContent(new BufferedReader(new InputStreamReader(in)));
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
