package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {

    public static String[] readInputToArray(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }

        return lines.toArray(new String[0]);
    }

    public static byte[] fileToByteArray(File file) {
        byte[] fileBytes = new byte[(int) file.length()]; // 파일 크기만큼 배열 생성

        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(fileBytes); // 파일 내용 읽기
            if (bytesRead != fileBytes.length) {
                throw new IOException("Could not completely read the file");
            }
            return fileBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isValidHttpMethod(String httpMethod) {
        Set<String> httpMethods = Set.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "CONNECT", "PATCH");
        return httpMethods.contains(httpMethod);
    }
}
