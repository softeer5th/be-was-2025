package http.request;

import http.enums.HttpMethod;
import http.enums.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;

public class HttpRequest {
    private HttpMethod method;
    private TargetInfo target;
    private HttpVersion version;

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(InputStream in) throws IOException, URISyntaxException {
        parseRequest(in);
    }

    private void parseRequest(InputStream in) throws IOException, URISyntaxException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8")); // InputStream => InputStreamReader => BufferedReader
        String startLine = HttpRequestParser.parseRequest(br);
        parseStartLine(startLine);
    }

    private void parseStartLine(String startLine) throws UnsupportedEncodingException, URISyntaxException {
        startLine = startLine.trim();
        String[] token = startLine.split(" +");

        if (token.length != 3) {
            this.method = HttpMethod.INVALID;
            this.target = null;
            this.version = HttpVersion.INVALID;
        } else {
            this.method = HttpMethod.getMethodFromString(token[0]);
            this.target = new TargetInfo(token[1]);
            this.version = HttpVersion.getVersionFromString(token[2]);
        }

        logger.debug("Start Line: " + method + " " + target + " " + version);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public TargetInfo getTarget() {
        return target;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public boolean isInvalid() {
        return method == HttpMethod.INVALID && target == null && version == HttpVersion.INVALID;
    }
}
