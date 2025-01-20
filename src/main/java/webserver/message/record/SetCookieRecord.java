package webserver.message.record;

import java.time.LocalDateTime;

public record SetCookieRecord(
        String name,
        String value,
        LocalDateTime expires
) { }
