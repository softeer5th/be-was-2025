package global.util;

import global.model.CommonResponse;

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

    public static String toJson(CommonResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"isSuccess\":").append(response.isSuccess()).append(",");
        sb.append("\"code\":").append(response.code() == null ? "null" : "\"" + escape(response.code()) + "\"").append(",");
        sb.append("\"message\":").append(response.message() == null ? "null" : "\"" + escape(response.message()) + "\"").append(",");
        sb.append("\"data\":").append(response.data() == null ? "null" : "\"" + escape(String.valueOf(response.data())) + "\"");
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