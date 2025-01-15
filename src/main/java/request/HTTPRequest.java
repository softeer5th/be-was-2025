package request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPRequest {
    private String httpMethod;
    private String uri;
    private String httpVersion;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, List<String>> queryStringParameters = new HashMap<>();
    private Map<String, String> pathVariables = new HashMap<>();
    private Map<String, String> bodyParameters = new HashMap<>();

    public String getHttpMethod() {return httpMethod;}
    public void setHttpMethod(String httpMethod) {this.httpMethod = httpMethod;}

    public String getUri() {return uri;}
    public void setUri(String uri) {this.uri = uri;}

    public String getHttpVersion() {return httpVersion;}
    public void setHttpVersion(String httpVersion) {this.httpVersion = httpVersion;}

    public String getHeaderByKey(String key){return headers.get(key);}
    public Map<String, String> getHeaders() {return headers;}
    public void setHeaders(Map<String, String> headers) {this.headers = headers;}

    public String getSingleQueryStringByKey(String key){return queryStringParameters.get(key).get(0);}
    public List<String> getAllQueryStringsByKey(String key){return queryStringParameters.get(key);}
    public Map<String, List<String>> getAllQueryStrings() {return queryStringParameters;}
    public void setQueryString(String key, String value) {
        queryStringParameters.putIfAbsent(key, new ArrayList<>());
        queryStringParameters.get(key).add(value);
    }

    public String getPathVariableByKey(String key){return pathVariables.get(key);}
    public Map<String, String> getAllPathVariables() {return pathVariables;}
    public void setPathVariable(String key, String value){pathVariables.put(key, value);}

    public String getBodyParameterByKey(String key){return bodyParameters.get(key);}
    public Map<String, String> getAllBodyParameters() {return bodyParameters;}
    public void setBodyParameter(String key, String value){bodyParameters.put(key, value);}
    public void setBodyParameters(Map<String, String> bodyParameters) {this.bodyParameters = bodyParameters;}


}
