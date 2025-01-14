package util;

import java.util.HashMap;
import java.util.Map;

public class ParameterParser {
    private static final Map<String, String> parameterMap = new HashMap<>();

    public ParameterParser(String parameter) throws IllegalArgumentException{
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

    public String getId(){
        return parameterMap.get("id");
    }

    public String getName(){
        return parameterMap.get("name");
    }

    public String getPassword(){
        return parameterMap.get("password");
    }
}
