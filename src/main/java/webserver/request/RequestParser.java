package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.cookie.Cookie;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public static Request parse(InputStream in) throws IOException {
        Request request = new Request();
        List<String> headers = new ArrayList<>();

        String startLine = readLine(in);
        if (startLine == null) {
            return request;
        }
        String line = readLine(in);
        while (line != null && !line.isEmpty()) {
            headers.add(line);
            line = readLine(in);
        }
        setStartLine(request, startLine);
        setHeaders(request, headers);
        setCookie(request);

        String contentLengthHeader = request.getHeader("CONTENT-LENGTH");
        if (contentLengthHeader == null) { return request; }

        String boundary = getBoundaryIfMultipart(request.getHeader("CONTENT-TYPE"));
        if (boundary != null) {
            setMultiPartBody(request, boundary, in);
            return request;
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        byte[] bodyBytes = new byte[contentLength];
        int read = in.read(bodyBytes);
        if (read > 0) {
            request.setBody(new String(bodyBytes, 0, read, StandardCharsets.UTF_8));
        }

        return request;
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int c;
        while ((c = in.read()) != -1) {
            if (c == '\r') {
                int next = in.read();
                if (next == '\n') {
                    break;
                }
                if (next == -1) {
                    buffer.write('\r');
                    break;
                }
                buffer.write('\r');
                buffer.write(next);
            } else if (c == '\n') {
                break;
            } else {
                buffer.write(c);
            }
        }
        if (c == -1 && buffer.size() == 0) {
            return null;
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void setStartLine(Request request, String line) {
        request.setRequestLine(line);
        String[] tokens = line.split(" ");
        request.setMethod(tokens[0]);
        request.setUrl(tokens[1]);
        String[] parts = tokens[1].split("\\?");
        if (parts.length > 1) {
            request.setUrl(parts[0]);
            request.setParameter(parts[1]);
        } else {
            request.setContentType(parts[0]);
        }
    }

    private static void setHeaders(Request request, List<String> headers) {
        for (String header : headers) {
            try {
                String[] tokens = header.split(":", 2);
                String key = tokens[0].trim().toUpperCase();
                String value = tokens[1].trim();
                request.addHeader(key, value);
            } catch (Exception e) {
                logger.error("Invalid Header");
            }
        }
    }

    private static void setCookie(Request request) {
        String cookieString = request.getHeader("COOKIE");
        if (cookieString != null) {
            Cookie cookie = new Cookie(cookieString);
            request.setCookie(cookie);
        }
    }

    private static String getBoundaryIfMultipart(String contentTypeString) {
        if (contentTypeString == null) {
            return null;
        }
        String[] tokens = contentTypeString.split(";");
        if (!tokens[0].equalsIgnoreCase("multipart/form-data")) {
            return null;
        }
        return tokens[1].split("=", 2)[1].trim();
    }

    private static void setMultiPartBody(Request request, String boundary, InputStream in) throws IOException {
        List<FileBody> files = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String name = null;
        String filename = null;
        String contentType = null;

        while (true) {
            String headerLine = readLine(in);
            if (headerLine == null || headerLine.equals("--")) {
                break;
            }
            if (headerLine.startsWith("--" + boundary)) {

                continue;
            }
            if (headerLine.isEmpty()) {
                if (filename != null && !filename.isEmpty()) {
                    byte[] fileBytes = readFileBytesUntilBoundary(in, boundary);
                    files.add(new FileBody(name, filename, contentType, fileBytes));
                    name = null;
                    filename = null;
                    contentType = null;
                } else if (name != null) {
                    String fieldValue = readFieldValueUntilBoundary(in, boundary);
                    sb.append(name).append("=").append(fieldValue).append("&");
                    name = null;
                }
            } else {
                if (headerLine.contains("Content-Disposition:")) {
                    if (headerLine.contains("filename=")) {
                        name = extractValue(headerLine, "name");
                        filename = extractValue(headerLine, "filename");
                    } else {
                        name = extractValue(headerLine, "name");
                    }
                } else if (headerLine.contains("Content-Type:")) {
                    contentType = headerLine.split(":", 2)[1].trim();
                }
            }

        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        request.setFiles(files);
        request.setBody(sb.toString());
    }

    private static byte[] readFileBytesUntilBoundary(InputStream in, String boundary) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] boundaryMarker = ("\r\n--" + boundary).getBytes(StandardCharsets.UTF_8);
        byte[] boundaryMarkerEnd = ("--" + boundary).getBytes(StandardCharsets.UTF_8);

        while (true) {
            in.mark(1);
            int b = in.read();
            if (b == -1) {
                break;
            }
            baos.write(b);
            byte[] current = baos.toByteArray();
            if (endsWith(current, boundaryMarker) || endsWith(current, boundaryMarkerEnd)) {
                int removeLen = endsWith(current, boundaryMarker) ? boundaryMarker.length : boundaryMarkerEnd.length;
                int newSize = current.length - removeLen;
                baos.reset();
                baos.write(current, 0, newSize);
                break;
            }
        }
        return baos.toByteArray();
    }

    private static String readFieldValueUntilBoundary(InputStream in, String boundary) throws IOException {
        byte[] data = readFileBytesUntilBoundary(in, boundary);
        return new String(data, StandardCharsets.UTF_8).trim();
    }

    private static boolean endsWith(byte[] data, byte[] suffix) {
        if (data.length < suffix.length) {
            return false;
        }
        for (int i = 0; i < suffix.length; i++) {
            if (data[data.length - suffix.length + i] != suffix[i]) {
                return false;
            }
        }
        return true;
    }

    private static String extractValue(String line, String key) {
        int start = line.indexOf(key + "=\"") + key.length() + 2;
        int end = line.indexOf("\"", start);
        return line.substring(start, end);
    }
}
