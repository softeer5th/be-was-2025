package http.session;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private Map<String, Object> attributes = new HashMap<>();

    public boolean hasAttribute(String key){
        return attributes.containsKey(key);
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public void saveAttribute(String key, Object value){
        attributes.put(key, value);
    }
}
