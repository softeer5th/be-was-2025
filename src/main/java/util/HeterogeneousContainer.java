package util;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HeterogeneousContainer {
    private final Map<String, Object> map;
    public HeterogeneousContainer(Map<String, Object> map) {
        this.map = map;
    }
    public void put(String key, String value) {
        map.put(key, value);
    }
    public Optional<String> get(String key) {
        Object value = map.get(key);
        if (!(value instanceof String)) {
            return Optional.empty();
        }
        return Optional.of((String) value) ;
    }
    public <T> void put(String key, T value, Class<T> type) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        map.put(key, value);
    }
    public <T> Optional<T> get(String key, Class<T> type) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(type);
        Object value = map.get(key);
        if (!type.isInstance(value)) {
            return Optional.empty();
        }
        type.cast(value);
        return Optional.of(type.cast(value));
    }
}
