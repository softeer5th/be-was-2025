package util;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class ParsingUtil {
    public static List<String> parseRequestHeader(BufferedReader br) throws Exception {
        List<String> lines = new ArrayList<>();
        String line;
        while (true) {
            line = br.readLine();
            if (line == null || line.isEmpty()) break;
            lines.add(line);
        }
        return lines;
    }
}