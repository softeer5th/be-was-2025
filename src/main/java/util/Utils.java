package util;

import response.HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final int SESSION_ID_LENGTH = 32;
    private static final String SID = "sid=";
    private static final Set<String> httpMethods = Set.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "CONNECT", "PATCH");

    public static String[] readInputToArray(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        StringBuilder body = new StringBuilder();
        int contentLength = 0;
        boolean isBody = false;


        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            if (line.isEmpty()) {
                isBody = true;
                lines.add("");
                break;
            }
            lines.add(line);
            if (line.toLowerCase().startsWith("content-length")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        if (isBody && contentLength > 0) {
            char[] buffer = new char[contentLength];
            int read = reader.read(buffer, 0, contentLength);
            if (read > 0) {
                body.append(buffer, 0, read);
            }
            lines.add(body.toString());
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

    public static boolean isValidHttpMethod(String httpMethod) {
        if (httpMethods.contains(httpMethod)) {
            return true;
        }
        return false;
    }

    public static boolean isValidHeader(String[] parts) {
        if(parts.length != 3){
            return false;
        }
        return true;
    }

    public static String removeLastSlash(String resourceName) {
        if (resourceName != null && resourceName.endsWith("/")) {
            return resourceName.substring(0, resourceName.length() - 1);
        }
        return resourceName;
    }

    public static void flushResponse(HTTPResponse httpResponse, DataOutputStream dos) throws IOException {
        // 응답 라인 작성
        dos.writeBytes(httpResponse.getHttpVersion() + " "
                + httpResponse.getHttpCode().getStatusCode() + " "
                + httpResponse.getHttpCode().getReasonPhrase() + "\r\n");

        // 헤더 작성
        for (Map.Entry<String, String> header : httpResponse.getHeaders().entrySet()) {
            dos.writeBytes(header.getKey() + ": " + header.getValue() + "\r\n");
        }

        dos.writeBytes("\r\n");

        // 바디 작성
        if (httpResponse.getBody() != null) {
            if (httpResponse.getBody() instanceof String) {
                dos.writeBytes((String) httpResponse.getBody());
            } else if (httpResponse.getBody() instanceof byte[]) {
                dos.write((byte[]) httpResponse.getBody());
            }
        }

        dos.flush();
    }


    public static String generateSessionID() {
        return UUID.randomUUID().toString();
    }

    public static String getSessionIdInCookie(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            return null;
        }

        String searchKey = SID;
        int startIndex = cookie.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }

        startIndex += searchKey.length();
        int endIndex = startIndex;

        while (endIndex < cookie.length() && cookie.charAt(endIndex) != ';' && cookie.charAt(endIndex) != ',') {
            endIndex++;
        }

        return cookie.substring(startIndex, endIndex);
    }
}
