package exception;

/**
 * 로그인 관련 오류를 처리하는 예외 클래스입니다.
 * <p>
 * 이 클래스는 {@link ClientErrorException}을 상속하며, 로그인 과정에서 발생하는 다양한 오류를 처리합니다.
 * 주로 잘못된 로그인 정보나 인증 문제를 나타낼 때 사용됩니다.
 * </p>
 */
public class LoginException extends ClientErrorException {

    /**
     * LoginException 생성자
     *
     * @param errorCode 오류를 나타내는 {@link ErrorCode} 객체
     */
    public LoginException(ErrorCode errorCode) {
        super(errorCode);
    }
}
