package webserver.httpserver.header;

import exception.BadRequestException;
import webserver.httpserver.ContentType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MimeType {

    private ContentType type;
    private final Map<String, String> attributeVariables = new HashMap<>();
    public MimeType(String contentType) {
        String[] contentTypeParts = contentType.split(";");
        type = ContentType.getContentType(contentTypeParts[0].trim());
        // 이부분 공백처리 정교화 필요 TODO
        for (int i = 1; i < contentTypeParts.length; i++) {
            String[] attributeVariable = contentTypeParts[i].split("=");
            if(attributeVariable.length != 2){
                throw new BadRequestException("Content-Type 헤더가 올바르지 않음");
            }
            attributeVariables.put(attributeVariable[0].trim().toLowerCase(), attributeVariable[1].trim());
        }
    }

    public ContentType getType() {
        return type;
    }

    public Optional<String> getAttributeVariable(String key){
        return Optional.of(attributeVariables.get(key));
    }
}
