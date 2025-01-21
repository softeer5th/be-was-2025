package webserver.response.body;

import webserver.enums.ContentType;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

// HTTP 응답 Body를 나타내는 추상 클래스
public abstract class ResponseBody {

    // 빈 Body를 반환하는 팩토리 메서드
    public static ResponseBody empty() {
        return EmptyBody.INSTANCE;
    }

    // String 타입의 Body를 반환하는 팩토리 메서드
    public static ResponseBody of(String body) {
        return new StringBody(body);
    }

    // File 타입의 Body를 반환하는 팩토리 메서드
    public static ResponseBody of(File body) {
        return new FileBody(body);
    }

    public static ResponseBody of(byte[] body, ContentType contentType) {
        return new ByteBody(body, contentType);
    }

    // 클라이언트 OutputStream에 직접 body 내용을 보내는 메서드
    public abstract void writeBody(OutputStream out) throws IOException;

    // body의 길이를 반환하는 메서드
    public abstract Optional<Long> getContentLength();

    // body의 ContentType을 반환하는 메서드
    public abstract Optional<ContentType> getContentType();
}
