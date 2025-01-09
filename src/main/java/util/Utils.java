package util;

import constant.HTTPCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static webserver.RequestHandler.httpResponseHandler;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final Set<String> httpMethods = Set.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "CONNECT", "PATCH");

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
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isValidHttpMethod(String httpMethod, DataOutputStream dos) {
        if (httpMethods.contains(httpMethod)) {
            return true;
        }
        httpResponseHandler.responseFailHandler(dos, HTTPCode.METHOD_NOT_ALLOWED);
        return false;
    }
}
