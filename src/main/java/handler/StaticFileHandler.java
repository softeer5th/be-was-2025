package handler;

import enums.FileContentType;
import enums.HttpStatus;
import exception.ClientErrorException;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.FileReader;

import static exception.ErrorCode.FILE_NOT_FOUND;

/**
 * 정적 파일 요청을 처리하는 핸들러 클래스.
 * <p>
 * 이 핸들러는 요청된 파일 경로에 해당하는 정적 파일을 읽고, 적절한 HTTP 응답을 생성합니다.
 * 파일이 존재하지 않을 경우 {@link ClientErrorException}을 발생시킵니다.
 * </p>
 */
public class StaticFileHandler implements Handler {

    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");

    /**
     * 클라이언트의 요청에 해당하는 정적 파일을 읽어 HTTP 응답을 생성합니다.
     * <p>
     * 파일 경로에서 확장자를 추출하고, 해당 확장자에 맞는 `Content-Type`을 설정한 후,
     * 파일을 읽어 HTTP 응답의 본문으로 설정합니다. 파일이 존재하지 않으면 {@link ClientErrorException}
     * 이 발생합니다.
     * </p>
     *
     * @param request 클라이언트의 HTTP 요청 정보
     * @return 요청된 파일을 포함하는 {@link HttpResponse}
     * @throws ClientErrorException 파일이 존재하지 않을 경우 발생
     */
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        String path = request.getPath();

        // 파일 경로에서 확장자 추출
        FileContentType extension = FileContentType.getExtensionFromPath(path);

        HttpResponse response = new HttpResponse();

        // 응답 상태 코드 및 Content-Type 설정
        response.setStatus(HttpStatus.OK);
        response.setContentType(extension);

        // 파일을 읽어 응답 본문에 설정, 파일이 없으면 예외 발생
        byte[] body = FileReader.readFile(STATIC_FILE_PATH + path)
                .orElseThrow(() -> new ClientErrorException(FILE_NOT_FOUND));
        response.setBody(body);

        return response;
    }
}
