package http.enums;

public enum HttpStatus {
    OK(200, "OK"), NOT_FOUND(404, "Not Found");

    private int statusCode;
    private String reasonPhrase;

    HttpStatus(int statusCode, String reasonPhrase){
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode(){
        return this.statusCode;
    }

    public String getReasonPhrase(){
        return this.reasonPhrase;
    }
}
