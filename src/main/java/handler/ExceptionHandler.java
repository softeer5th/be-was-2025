package handler;

import exception.ClientErrorException;
import exception.ErrorCode;
import exception.ErrorException;
import util.FileReader;

/**
 * 예외를 처리하고 사용자에게 오류 페이지를 반환하는 클래스입니다.
 * <p>
 * 이 클래스는 발생한 예외에 맞는 HTTP 상태 코드와 메시지를 포함하는 오류 페이지를 생성하여 반환합니다.
 * </p>
 */
public class ExceptionHandler {

    // 정적 파일 경로 환경 변수에서 STATIC_FILE_PATH 값을 가져옴
    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");
    // 오류 HTML 파일 경로
    private static final String ERROR_HTML = "/error.html";
    private static final String MESSAGE_REPLACE_TARGET = "<!--message-->";
    private static final String CODE_REPLACE_TARGET = "<!--code-->";

    /**
     * 예외를 처리하여 오류 HTML 페이지를 반환합니다.
     * <p>
     * 주어진 예외를 처리하여, 해당 예외의 HTTP 상태 코드와 메시지를 포함하는 오류 페이지를 반환합니다.
     * </p>
     *
     * @param e 발생한 예외 (ErrorException)
     * @return 오류 페이지의 HTML 내용
     * @throws ClientErrorException 만약 오류 HTML 파일을 읽을 수 없다면 발생
     */
    public static String handle(ErrorException e) {
        // 오류 HTML 파일을 읽음
        String html = FileReader.readFileAsString(STATIC_FILE_PATH + ERROR_HTML)
                .orElseThrow(() -> new ClientErrorException(ErrorCode.FILE_NOT_FOUND));

        // 오류 코드와 메시지를 HTML에 삽입
        return html
                .replace(CODE_REPLACE_TARGET, String.valueOf(e.getHttpStatus().getCode()))
                .replace(MESSAGE_REPLACE_TARGET, e.getMessage());
    }
}
