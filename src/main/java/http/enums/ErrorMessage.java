package http.enums;

public enum ErrorMessage {
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    INVALID_PARAMETER("매개 변수가 잘못되었습니다."),
    INVALID_ID_PASSWORD("아이디나 비밀번호가 잘못되었습니다."),
    NOT_FOUND_PATH_AND_FILE("존재하지 않는 경로 및 파일입니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}