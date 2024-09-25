package com.cylorun.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ResourceUtil {

    public static String readFile(String name) throws FileNotFoundException {
        StringBuilder contents = new StringBuilder();
        URL url = ResourceUtil.class.getClassLoader().getResource(name);
        if (url == null) {
            throw new FileNotFoundException("File not found: " + name);
        }
        try (InputStream inputStream = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contents.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return contents.toString();
    }
}
