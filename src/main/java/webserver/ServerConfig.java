package webserver;

public class ServerConfig {
    private final int DEFAULT_PORT;
    private final int ThreadPoolSize;

    public ServerConfig() {
        this.DEFAULT_PORT = 8080;
        this.ThreadPoolSize = 10;
    }

    public int getDefaultPort(){
        return DEFAULT_PORT;
    }

    public int getThreadPoolSize(){
        return ThreadPoolSize;
    }
}
