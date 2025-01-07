package webserver.response.body;

import webserver.enums.ContentType;

import java.io.File;
import java.io.OutputStream;
import java.util.Optional;

// HTTP 응답 Body를 나타내는 추상 클래스
public abstract class Body {
    // 빈 Body를 반환하는 팩토리 메서드
    public static Body empty() {
        return new EmptyBody();
    }

    // String 타입의 Body를 반환하는 팩토리 메서드
    public static Body of(String string) {
        return new StringBody(string);
    }

    // File 타입의 Body를 반환하는 팩토리 메서드
    public static Body of(File file) {
        return new FileBody(file);
    }

    // 클라이언트 OutputStream에 직접 body 내용을 보내는 메서드
    public abstract void writeBody(OutputStream out);

    // body의 길이를 반환하는 메서드
    public abstract Long getContentLength();

    // body의 ContentType을 반환하는 메서드
    public abstract Optional<ContentType> getContentType();
}
