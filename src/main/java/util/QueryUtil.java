package util;

import exception.BaseException;
import exception.HttpErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class QueryUtil {
    private static final Logger logger = LoggerFactory.getLogger(QueryUtil.class);

    public static Map<String, String> parseQueryParams(String query, int size) throws BaseException {
        Map<String, String> params = new HashMap<>();
        if (query.isEmpty()) {
            logger.error("Query string is empty");
            throw new BaseException(HttpErrorCode.INVALID_QUERY_PARAM);
        }
        String[] pairs = query.split("&");
        if (pairs.length != size) {
            logger.error("Query pair size is not {}", size);
            throw new BaseException(HttpErrorCode.INVALID_QUERY_PARAM);
        }
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            } else {
                logger.error("Query string is not pair");
                throw new BaseException(HttpErrorCode.INVALID_QUERY_PARAM);
            }
        }
        return params;
    }
}
