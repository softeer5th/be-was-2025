package Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QueryParameters {
    private final Map<String, String> parameters;

    public QueryParameters(String query) {
        parameters = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] tokens = pair.split("=");
            add(tokens[0], tokens[1]);
        }
    }

    public String get(String key) {
        return parameters.get(key);
    }

    public Set<String> getKeySet() {
        return parameters.keySet();
    }

    private void add(String key, String value) {
        parameters.put(key, value);
    }
}