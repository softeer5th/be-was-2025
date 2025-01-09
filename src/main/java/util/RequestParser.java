package util;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private final List<String> requests = new ArrayList<>();
    public String url = "/";
    public String extension = "html";
    public String parameter = null;

    public RequestParser(InputStream in){
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String line = br.readLine();
            while (!line.isEmpty()) {
                requests.add(line);
                line = br.readLine();
            }
            String url = requests.get(0).split(" ")[1];
            setUrl(url);
            String[] parts = url.split("\\?");
            if(parts.length > 1){
                setParameter(parts[1]);
            }
            else{
                setContentType(parts[0]);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setUrl(String url){
        this.url = url;
    }

    private void setContentType(String url){
        String[] tokens = url.split("\\.");
        if(tokens.length > 1) {
            this.extension = tokens[1];
        }
    }

    private void setParameter(String parameter){
        this.parameter = parameter;
    }

    public void getLogs(Logger logger){
        logger.debug("request: ");
        for(String request : requests){
            logger.debug(request);
        }
    }
}
