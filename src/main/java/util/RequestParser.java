package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public abstract class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public static RequestInfo parse(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(inputStream));

        String requests = br.readLine();
        String[] requestInfo = requests.split(" ");
        HttpMethod method = HttpMethod.match(requestInfo[0]);
        String url = requestInfo[1];

        while (!(requests = br.readLine()).equals("")) {
            logger.debug("header = {}",requests);
        }

        return new RequestInfo(method, url);
    }
}
