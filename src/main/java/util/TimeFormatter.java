package util;

import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static DateTimeFormatter formatter() {
        return formatter;
    }
}
