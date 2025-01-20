package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ResponseWriter {
    private static final Logger logger = LoggerFactory.getLogger(ResponseWriter.class);

    static private class ResponseStringBuilder {
        StringBuilder sb = new StringBuilder();

        void append(String str) {
            sb.append(str);
        }
        void appendWithSpace(String str) {
            sb.append(str);
            sb.append(" ");
        }

        void appendHeader(String name, String value) {
            sb.append(name);
            sb.append(": ");
            sb.append(value);
            appendNewLine();
        }
        void appendLine(String line) {
            sb.append(line);
            sb.append("\r\n");
        }
        void appendNewLine() {
            sb.append("\r\n");
        }
        String build() {
            return sb.toString();
        }
    }

    static void write(DataOutputStream out, HTTPRequest request, HTTPResponse response) {
        ResponseStringBuilder builder = new ResponseStringBuilder();
        try {
            builder.appendWithSpace(request.getVersion().toString());
            builder.appendWithSpace(response.getStatusCode().toString());
            builder.appendNewLine();
            builder.appendHeader("Content-Type", response.getContentType().toString());
            builder.appendHeader("Content-Length", String.valueOf(response.getBody().length));
            Set<Map.Entry<String, String>> entries = response.getHeaders().entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.appendHeader(entry.getKey(), entry.getValue());
            }
            builder.appendNewLine();
            out.writeBytes(builder.build());
            out.write(response.getBody(), 0, response.getBody().length);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
