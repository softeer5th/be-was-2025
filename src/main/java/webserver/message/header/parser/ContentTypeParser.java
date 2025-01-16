package webserver.message.header.parser;

import util.HeterogeneousContainer;
import webserver.enumeration.HTTPContentType;
import webserver.exception.HTTPException;
import webserver.message.header.records.ContentTypeRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentTypeParser implements HeaderParser {
    private static final String CONTENT_TYPE_REGEX = "^(?<type>[^/]+/?)(?<subtype>[^;]+)(?<parameters>.*)$";
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX);
    private static final String PARAMETER_REGEX = ";\\s*(?<key>[^=]+)=(?<value>[^;]+)";
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_REGEX);

    @Override
    public void parse(HeterogeneousContainer headers, String value) {
        Matcher matcher = CONTENT_TYPE_PATTERN.matcher(value);
        if (!matcher.find()) {
            throw new HTTPException.Builder()
                    .causedBy(ContentTypeParser.class)
                    .badRequest("Invalid Content-Type header: " + value);
        }
        String subtype = matcher.group("subtype");
        HTTPContentType contentType = HTTPContentType.fromDetailType(subtype);
        Map<String, String> parameters = readParameters(matcher.group("parameters"));
        ContentTypeRecord record = getContentTypeRecord(parameters, contentType);
        headers.put("content-type", record, ContentTypeRecord.class);
    }

    private Map<String, String> readParameters(String parameters) {
        Matcher matcher = PARAMETER_PATTERN.matcher(parameters);
        Map<String, String> parametersMap = new HashMap<>();
        while (matcher.find()) {
            String key = matcher.group("key");
            String value = matcher.group("value");
            parametersMap.put(key, value);
        }
        return parametersMap;
    }

    private ContentTypeRecord getContentTypeRecord(Map<String, String> paramMap, HTTPContentType contentType) {
        String charset = paramMap.get("charset");
        if (charset != null) {
            charset = charset.toLowerCase();
        }
        return new ContentTypeRecord(
                contentType,
                charset,
                paramMap.get("boundary")
        );
    }
}
