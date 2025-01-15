package http.enums;

public enum HttpStatus {
    OK(200, "OK"),
    SEE_OTHER(303, "See Other"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

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
