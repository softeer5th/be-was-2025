package global.model;

public record CommonResponse(boolean isSuccess, String code, String message, Object data) {
}
