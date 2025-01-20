package webserver.message.header.records;

import webserver.enumeration.HTTPContentType;

public record ContentTypeRecord(
        HTTPContentType contentType,
        String charset,
        String boundary
) {
}
