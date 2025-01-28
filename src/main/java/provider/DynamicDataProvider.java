package provider;

import java.util.Map;

public interface DynamicDataProvider {
    Model provideData(Map<String, Object> params);
}
