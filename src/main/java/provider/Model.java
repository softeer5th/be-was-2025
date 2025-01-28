package provider;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Model {
    private Map<String, Object> data = new LinkedHashMap<>();

    public void put(String key, Object value){
        data.put(key, value);
    }

    public Object get(String key){
        return data.get(key);
    }

    public Map<String, Object> getData(){
        return data;
    }
}
