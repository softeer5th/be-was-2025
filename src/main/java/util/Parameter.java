package util;

import java.util.HashMap;
import java.util.Map;

public class Parameter {
    private final Map<String, String> parameterMap = new HashMap<>();

    public Parameter(String parameter) throws IllegalArgumentException{
        try {
            String[] params = parameter.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                parameterMap.put(keyValue[0], keyValue[1]);
            }
        } catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("Invalid parameter: " + parameter);
        }
    }

    public String getValue(String key) {
        return parameterMap.get(key);
    }

    public static String setPostId(String path, int postId) {
        return path + "?postid=" + postId;
    }
}
