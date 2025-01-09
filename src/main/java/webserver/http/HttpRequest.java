package webserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String request;

    private final String method;

    private final String url;

    private final String version;

    private final Map<String, String> headers = new HashMap<>();

    private final String body;

    public HttpRequest(InputStream in) throws IOException {
        String request = parse(in);
        this.request = request;
        String[] lines = request.strip().split("\n");
        method = lines[0].split(" ")[0].trim();
        url = lines[0].split(" ")[1].trim();
        version = lines[0].split(" ")[2].trim();

        for (int i = 2 ; i < lines.length; i++) {
            String[] elements = lines[i].split(":");
            String name = elements[0].trim();
            String value = elements[1].trim();
            headers.put(name, value);
        }

        body = "";
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public String toString(){
        return request;
    }

    private String parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) break;

            stringBuilder.append(line).append("\r\n");
        }
        return stringBuilder.toString();
    }

}

