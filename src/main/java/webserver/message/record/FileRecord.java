package webserver.message.record;

import webserver.enumeration.HTTPContentType;

public record FileRecord(byte [] bytes, HTTPContentType contentType, String fileName) {
}
