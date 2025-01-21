package webserver.message.record;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SetCookieRecord {
    public static class Builder {
        private String name = "";
        private String value = "";
        private Map<String, String> attributes = new HashMap<>();

        public Builder(String name, String value) {
            this.name = name;
            this.value = value;
        }
        public Builder path(String value) {
            attributes.put("Path", value);
            return this;
        }
        public Builder domain(String value) {
            attributes.put("Domain", value);
            return this;
        }
        public Builder expires(LocalDateTime dateTime) {
            String formattedDate = dateTime
                    .atZone(ZoneId.of("GMT"))
                    .format(DateTimeFormatter.RFC_1123_DATE_TIME);
            attributes.put("Expires", formattedDate);
            return this;
        }
        public SetCookieRecord build() {
            return new SetCookieRecord(name, value, attributes);
        }
    }
    private final String name;
    private final String value;
    private final Map<String, String> attributes;

    SetCookieRecord(String name, String value, Map<String, String> attributes) {
        this.name = name;
        this.value = value;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }
}
