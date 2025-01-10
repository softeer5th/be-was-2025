package global.util;

import java.util.Map;

public class JsonUtil {

    public static String toJson(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int cnt = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (cnt > 0) sb.append(",");
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            sb.append("\"").append(escape(entry.getValue())).append("\"");
            cnt++;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escape(String str) {
        if (str == null) return "";
        return str
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}