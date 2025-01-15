package webserver.message.header.parser;

import util.HeterogeneousContainer;
import webserver.enumeration.HTTPContentType;
import webserver.message.header.records.AcceptRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentTypeParser implements HeaderParser {
    private static final String ACCEPT_REGEX = "(?<ContentType>[^\\s,;]+/[^\\s,;]+)(?:;\\s*q=(?<qValue>[0-1].[0-9]+?))?";
    private static final Pattern ACCEPT_PATTERN = Pattern.compile(ACCEPT_REGEX);
    @Override
    public void parse(HeterogeneousContainer headers, String value) {
        Matcher matcher = ACCEPT_PATTERN.matcher(value);
        List<AcceptRecord> records = new ArrayList<>();
        while (matcher.find()) {
            String contentType = matcher.group("ContentType");
            String qValue = matcher.group("qValue");
            float q = qValue != null ? Float.parseFloat(qValue) : 1.0f;
            AcceptRecord record = new AcceptRecord(HTTPContentType.fromFullType(contentType), q);
            records.add(record);
        }
        headers.put("accept", records.toArray(new AcceptRecord[]{records.get(0)}), AcceptRecord[].class);
    }
}
