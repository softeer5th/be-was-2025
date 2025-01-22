package webserver.httpserver.header;

import exception.BadRequestException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContentDisposition {
    public static final String FORM_DATA = "form-data";
    public static final String NAME = "name";
    private String disposition;
    private final Map<String, String> attributeVariables = new HashMap<>();

    public ContentDisposition(String contentDisposition) {
        String[] contentDispositionParts = contentDisposition.split(";");
        disposition = contentDispositionParts[0].trim();
        for (int i = 1; i < contentDispositionParts.length; i++) {
            String[] attributeVariable = contentDispositionParts[i].split("=");
            if(attributeVariable.length != 2){
                throw new BadRequestException("Content-Disposition 헤더가 올바르지 않음");
            }
            attributeVariables.put(attributeVariable[0].trim().toLowerCase(), attributeVariable[1].trim());
        }
    }

    public String getDisposition() {
        return disposition;
    }

    public Optional<String> getName(){
        String s = attributeVariables.get(NAME);
        if (s == null) return Optional.empty();
        return Optional.of((s.substring(1, s.length() - 1)));
    }

    public Optional<String> getAttributeVariable(String key){
        String s = attributeVariables.get(key);
        if (s == null) return Optional.empty();
        return Optional.of((s.substring(1, s.length() - 1)));
    }
}
