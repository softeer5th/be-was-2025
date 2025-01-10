package requst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryString {
    private Map<String, List<String>> queryMap = new HashMap<>();
    public QueryString(String uri) {
        String query = uri.substring(uri.indexOf("?") + 1);
        String[] pairs = query.split("&");
        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryMap.computeIfAbsent(keyValue[0], k -> new ArrayList<>()).add(keyValue[1]);
            }
        }
    }

    public String getSingleValueByKey(String key) {
        return queryMap.get(key) != null ? queryMap.get(key).get(0) : null;
    }

    public List<String> getAllValuesByKey(String key) {
        return queryMap.get(key);
    }

    public void setValueByKey(String key, List<String> value) {
        queryMap.put(key, value);
    }

    public void setValueByKey(String key, String value) {
        queryMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

}
