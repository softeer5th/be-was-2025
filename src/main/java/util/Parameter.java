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

    public String getId(){
        return parameterMap.get("id");
    }

    public String getName(){
        return parameterMap.get("name");
    }

    public String getPassword(){
        return parameterMap.get("password");
    }

    public String getEmail(){
        return parameterMap.get("email");
    }
}
