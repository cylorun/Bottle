package com.cylorun.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

    public static String readFile(String path) throws FileNotFoundException {
        StringBuilder contents = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
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
