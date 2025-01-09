package http.request;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class TargetInfo {
    String path;
    Map<String, String> params;

    public TargetInfo(String uriString) throws URISyntaxException, UnsupportedEncodingException {
        parseUri(uriString);
    }

    public TargetInfo(String path, Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    private void parseUri(String uriString) throws URISyntaxException, UnsupportedEncodingException {
        URI uri = new URI(uriString);
        path = uri.getPath();
        params = new HashMap<>();
        if (uri.getQuery() != null) {
            for (String key : uri.getQuery().split("&")) {
                String[] keyValue = key.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
