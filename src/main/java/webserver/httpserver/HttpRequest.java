package webserver.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.header.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private HttpMethod method;
    private String uri;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private Cookie cookie;
    private String protocol;
    private byte[] body;

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private HttpRequest(
            HttpMethod method,
            String uri,
            String protocol,
            Map<String, String> headers,
            Map<String, String> parameters,
            Cookie cookie,
            byte[] body
    ) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.headers = headers;
        this.parameters = parameters;
        this.cookie = cookie;
        this.body = body;
    }

    public Optional<String> getHeader(String key) {
        return Optional.of(headers.get(key));
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public String getUri() {
        return uri;
    }

    public byte[] getBody() {
        return body;
    }


    public static class Builder{
        private HttpMethod method = null;
        private String uri = "";
        private String protocol = "";
        private final Map<String, String> headers = new HashMap<>();
        private final Map<String, String> parameters = new HashMap<>();
        private Cookie cookie = Cookie.NULL_COOKIE;
        private byte[] body = new byte[0];

        public Builder() {
        }

        public Builder method(HttpMethod httpMethod){
            this.method = httpMethod;
            return this;
        }

        public Builder uri(String uri){
            this.uri = uri;
            return this;
        }

        public Builder protocol(String protocol){
            this.protocol = protocol;
            return this;
        }

        public Builder addHeader(String key, String value){
            headers.put(key, value);
            return this;
        }

        public Builder addParameter(String key, String value){
            parameters.put(key, value);
            return this;
        }

        public Builder cookie(Cookie cookie){
            this.cookie = cookie;
            return this;
        }

        public Builder body(byte[] body){
            this.body = body;
            return this;
        }

        public HttpRequest build(){
            return new HttpRequest(method, uri, protocol, headers, parameters, cookie, body);
        }
    }
}

