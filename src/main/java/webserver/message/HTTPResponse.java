package webserver.message;

import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPStatusCode;
import webserver.enumeration.HTTPVersion;

public class HTTPResponse {
    static public class Builder {
        private HTTPVersion version;
        private HTTPStatusCode statusCode;
        private HTTPContentType contentType = HTTPContentType.DEFAULT_TYPE();
        private byte[] body;

        public Builder version(HTTPVersion version) {
            this.version = version;
            return this;
        }
        public Builder statusCode(HTTPStatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        public Builder contentType(HTTPContentType type) {
            this.contentType = type;
            return this;
        }
        public Builder contentTypeFromUri(String uri) {
            final int lastDot = uri.lastIndexOf(".");
            if (lastDot == -1) {
                return this;
            }
            final String postfix = uri.substring(lastDot + 1);
            this.contentType = HTTPContentType.fromDetailType(postfix);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HTTPResponse build() {
            return new HTTPResponse(version, statusCode, contentType, body);
        }
    }
    private final HTTPVersion version;
    private final HTTPStatusCode statusCode;
    private final HTTPContentType contentType;
    private final byte[] body;

    private HTTPResponse(HTTPVersion version, HTTPStatusCode statusCode, HTTPContentType contentType, byte[] body) {
        this.version = version;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public HTTPVersion getVersion() { return this.version; }
    public HTTPStatusCode getStatusCode() { return this.statusCode; }
    public HTTPContentType getContentType() { return this.contentType; }
    public byte[] getBody() { return this.body; }
}
