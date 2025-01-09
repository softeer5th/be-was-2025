package Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryParameters {
    Map<String, String> map;

    public QueryParameters(String query) {
        map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] tokens = pair.split("=");
            add(tokens[0], tokens[1]);
        }
    }

    public String get(String key) {
        return map.get(key);
    }

    public Set<String> getKeySet() {
        return map.keySet();
    }

    private void add(String key, String value) {
        map.put(key, value);
    }
}