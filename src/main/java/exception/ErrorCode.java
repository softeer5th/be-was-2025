package exception;

public enum ErrorCode {
    ALREADY_EXIST_USER_ID("이미 존재하는 사용자 아이디입니다."),
    ALREADY_EXIST_USER_NAME("이미 존재하는 사용자 이름입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
