package exception;

public enum ErrorCode {
    ALREADY_EXIST_LOGIN_ID("이미 존재하는 사용자 아이디입니다."),
    ALREADY_EXIST_USER_NAME("이미 존재하는 사용자 이름입니다."),
    NOT_FOUND_USER_BY_USER_ID("사용자 아이디와 일치하는 사용자가 없습니다."),
    MISMATCH_PASSWORD("비밀번호가 일치하지 않습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
