package nl.wholesale_iptv.launcher2.helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileHelper {
    public static String readFile(String path) throws IOException {
        StringBuilder fileData = new StringBuilder();
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(path));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}
