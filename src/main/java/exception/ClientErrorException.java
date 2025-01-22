package exception;

/**
 * 클라이언트 측 오류를 처리하는 예외 클래스입니다.
 * 이 클래스는 {@link ErrorException}을 확장하여 클라이언트에서 발생한 오류를 나타냅니다.
 * 클라이언트 오류의 코드와 메시지를 포함한 {@link ErrorCode}를 매개변수로 받습니다.
 */
public class ClientErrorException extends ErrorException {

    /**
     * ClientErrorException 생성자
     *
     * @param errorCode 클라이언트 오류 코드 ({@link ErrorCode} 열거형)
     */
    public ClientErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
