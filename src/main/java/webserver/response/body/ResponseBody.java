package webserver.response.body;

import webserver.enums.ContentType;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * HTTP 응답 Body를 나타내는 추상 클래스
 * Body의 종류, 길이를 나타내고, OutputStream에 body 내용을 쓰는 메서드를 제공한다.
 */
public abstract class ResponseBody {

    /**
     * 빈 Body를 반환하는 팩토리 메서드
     *
     * @return EmptyBody
     */
    public static ResponseBody empty() {
        return EmptyBody.INSTANCE;
    }

    /**
     * File 타입의 Body를 반환하는 팩토리 메서드
     *
     * @param body Body 에 담을 파일
     * @return FileBody
     */
    public static ResponseBody of(File body) {
        return new FileBody(body);
    }

    /**
     * byte 배열 타입의 Body를 반환하는 팩토리 메서드
     *
     * @param body        byte 배열
     * @param contentType body의 content type
     * @return ByteBody
     */
    public static ResponseBody of(byte[] body, ContentType contentType) {
        return new ByteBody(body, contentType);
    }

    /**
     * 클라이언트 OutputStream에 body 내용을 쓰는 메서드
     *
     * @param out 클라이언트 OutputStream
     * @throws IOException 클라이언트 OutputStream에 쓰는 중 발생하는 IOException
     */
    public abstract void writeBody(OutputStream out) throws IOException;

    /**
     * body의 길이를 반환하는 메서드
     *
     * @return body의 길이. body가 없는 경우 Optional.empty()
     */
    public abstract Optional<Long> getContentLength();

    /**
     * body의 ContentType을 반환하는 메서드
     *
     * @return body의 ContentType. body가 없는 경우 Optional.empty()
     */
    public abstract Optional<ContentType> getContentType();
}
