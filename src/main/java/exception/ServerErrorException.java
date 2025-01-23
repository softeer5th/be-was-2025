package exception;

/**
 * 서버에서 발생하는 오류를 처리하는 예외 클래스입니다.
 * <p>
 * 이 클래스는 {@link ErrorException}을 상속하며, 서버 측 오류에 해당하는 예외를 처리합니다.
 * 주로 서버 내부의 문제나 시스템 오류를 나타낼 때 사용됩니다.
 * </p>
 */
public class ServerErrorException extends ErrorException {

    /**
     * ServerErrorException 생성자
     *
     * @param errorCode 오류를 나타내는 {@link ErrorCode} 객체
     */
    public ServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
