package webserver.request;

import util.enums.Mime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Request {
    private final List<String> requests = new ArrayList<>();
    public String method;
    public String url = "/";
    public String extension = "html";
    public String parameter = null;
    public String contentType = "text/html";

    public Request(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        while (!line.isEmpty()) {
            requests.add(line);
            line = br.readLine();
        }
        parse();
    }

    private void parse(){
        String[] tokens = requests.get(0).split(" ");
        setMethod(tokens[0]);
        setUrl(tokens[1]);
        String[] parts = url.split("\\?");
        if(parts.length > 1){
            setUrl(parts[0]);
            setParameter(parts[1]);
        }
        else{
            setContentType(parts[0]);
        }
    }

    private void setMethod(String method){
        this.method = method;
    }

    private void setUrl(String url){
        this.url = url;
    }

    private void setContentType(String url){
        String[] tokens = url.split("\\.");
        if(tokens.length > 1) {
            this.extension = tokens[1];
            this.contentType = Mime.getByExtension(extension).getContentType();
        }
    }

    private void setParameter(String parameter){
        this.parameter = parameter;
    }

    public List<String> getRequests(){
        return requests;
    }
}
