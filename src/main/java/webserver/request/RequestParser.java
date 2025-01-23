package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.cookie.Cookie;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    public static Request parse(InputStream in) throws IOException {
        Request request = new Request();
        List<String> headers = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String startLine = br.readLine();
        String line = br.readLine();
        while (!line.isEmpty()) {
            headers.add(line);
            line = br.readLine();
        }
        setStartLine(request, startLine);
        setHeaders(request, headers);
        setCookie(request);

        String boundary = getBoundaryIfMultipart(request.getHeader("CONTENT-TYPE"));
        if(boundary != null) {
            setMultiPartBody(request, boundary, br);
            return request;
        }

        String contentLengthHeader = request.getHeader("CONTENT-LENGTH");

        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            char[] bodyChars = new char[contentLength];
            int read = br.read(bodyChars, 0, contentLength);
            if (read > 0) {
                String body = new String(bodyChars);
                request.setBody(body);
            }
        }


        return request;
    }

    private static void setStartLine(Request request, String line) {
        request.setRequestLine(line);
        String[] tokens = line.split(" ");
        request.setMethod(tokens[0]);
        request.setUrl(tokens[1]);
        String[] parts = tokens[1].split("\\?");
        if(parts.length > 1){
            request.setUrl(parts[0]);
            request.setParameter(parts[1]);
        }
        else{
            request.setContentType(parts[0]);
        }
    }

    private static void setHeaders(Request request, List<String> headers) {
        for(String header : headers){
            try {
                String[] tokens = header.split(":", 2);
                String key = tokens[0].trim().toUpperCase();
                String value = tokens[1].trim();
                request.addHeader(key, value);
            } catch (ArrayIndexOutOfBoundsException e) {logger.error("Invalid Header");}
        }
    }

    private static void setCookie(Request request){
        String cookieString = request.getHeader("COOKIE");
        if (cookieString != null) {
            Cookie cookie = new Cookie(cookieString);
            request.setCookie(cookie);
        }
    }

    private static String getBoundaryIfMultipart(String contentTypeString) {
        if (contentTypeString == null) { return null; }

        String[] tokens = contentTypeString.split(";");
        if(!tokens[0].equalsIgnoreCase("multipart/form-data")){ return null; }

        return tokens[1].split("=", 2)[1].trim();
    }

    private static void setMultiPartBody(Request request, String boundary, BufferedReader br) throws IOException {
        List<FileBody> files = new ArrayList<>();
        ByteArrayOutputStream fileData = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();

        String line;
        boolean isFilePart = false;
        String name = null;
        String filename = null;
        String contentType = null;

        while ((line = br.readLine()) != null) {
            if(line.startsWith("--" + boundary + "--")){ break;}
            if (line.startsWith("--" + boundary) || line.isEmpty()) {
                continue;
            }

            if (line.contains("Content-Disposition:")) {
                if (line.contains("filename=")) {
                    isFilePart = true;
                    name = extractValue(line, "name");
                    filename = extractValue(line, "filename");
                } else {
                    name = extractValue(line, "name");
                    isFilePart = false;
                }
            } else if (line.contains("Content-Type:")) {
                contentType = line.split(":", 2)[1].trim();
            } else if (isFilePart) {
                while ((line = br.readLine()) != null && !line.startsWith("--" + boundary)) {
                    fileData.write(line.getBytes(StandardCharsets.ISO_8859_1));
                    fileData.write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
                }
                byte[] data = fileData.toByteArray();
                fileData.reset();
                files.add(new FileBody(name, filename, contentType, data));
                isFilePart = false;
            } else {
                sb.append(name).append("=").append(line).append("&");
            }
        }

        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }

        request.setFiles(files);
        request.setBody(sb.toString());
    }

    private static String extractValue(String line, String key) {
        int start = line.indexOf(key + "=\"") + key.length() + 2;
        int end = line.indexOf("\"", start);
        return line.substring(start, end);
    }

}
