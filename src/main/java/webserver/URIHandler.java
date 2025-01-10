package webserver;

import constant.HTTPCode;
import manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static util.Utils.fileToByteArray;
import static webserver.RequestHandler.httpResponseHandler;

public class URIHandler {
    private static final String STATIC_FILE_DIRECTORY_PATH = "src/main/resources/static";
    private static final Logger logger = LoggerFactory.getLogger(URIHandler.class);
    private static final Map<String, Method> uriMethodMap = new HashMap<>();

    public URIHandler() {
        initMap();
    }

    private void initMap() {
        try{
            uriMethodMap.put("GET:/create", UserManager.class.getMethod("signUp", String.class, DataOutputStream.class));
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
    }

    // 동적 URI 가 존재하는지 확인
    public boolean handleDynamicRequest(String httpMethod, String uri, Object... params){
        Method method = uriMethodMap.get(generateUriMethodKey(httpMethod,uri));
        if(method != null){
            try {
                Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                method.invoke(instance, combineArgs(uri, params));
                logger.debug("Successfully invoked method: {} for URI: {} with params: {}",
                        method.getName(), uri, Arrays.toString(params));
                return true;
            } catch (Exception e) {
                logger.error("Fail to invoke method for " + uri);
            }
        }
        logger.debug("There is no dynamic request for " + uri);
        return false;
    }

    // 정적 리소스가 존재하는지 확인 후 없으면 400에러 발생
    public void handleStaticRequest(String uri, DataOutputStream dos){
        File file = new File(STATIC_FILE_DIRECTORY_PATH, uri);
        if (!file.exists() || file.isDirectory()) {
            file = new File(STATIC_FILE_DIRECTORY_PATH, uri + "/index.html");
            if (file.exists()) {
                byte[] body = fileToByteArray(file);
                httpResponseHandler.responseRedirectHandler(dos, HTTPCode.FOUND,uri + "/index.html");
                logger.debug("Successfully served static file for " + uri);
                return;
            } else {
                logger.debug("There is no static request for " + uri);
                httpResponseHandler.responseFailHandler(dos, HTTPCode.NOT_FOUND);
                return;
            }
        }

        byte[] body = fileToByteArray(file);
        httpResponseHandler.responseSuccessHandler(dos, HTTPCode.OK, uri, body);
        logger.debug("Successfully served static file for " + uri);
    }

    private Object[] combineArgs(String uri, Object... params) {
        Object[] combined = new Object[params.length + 1];
        combined[0] = uri;
        System.arraycopy(params, 0, combined, 1, params.length);
        return combined;
    }

    private String generateUriMethodKey(String httpMethod, String uri) {
        return httpMethod + ":" + uri.split("\\?")[0];
    }

}
