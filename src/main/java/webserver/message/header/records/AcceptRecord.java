package webserver.message.header.records;

import webserver.enumeration.HTTPContentType;

public record AcceptRecord (HTTPContentType type, float qValue) {
}
