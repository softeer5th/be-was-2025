package webserver.httpserver.header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CookieFactory {
    /**
     * 주어진 쿠키를 파싱하여 반환하는 팩토리 메소드.
     * 서버가 Request로 Cookie 헤더를 전달받았을 시, 파싱 후 Cookie 객체 형태로 저장.
     * @param values 헤더로 주어진 쿠키 문자열
     * @throws IllegalArgumentException 쿠키에 key-value 형태가 아닌 3개 이상의 인자가 들어가 있을 시
     * @return 쿠키를 생성하여 반환
     */
    public Cookie create(String values){
        Map<String, String> pairCookies = new HashMap<>();
        String[] cookieParts = values.split(";");
        for (String cookiePart : cookieParts) {
            String[] split = cookiePart.split("=");
            if (split.length > 2){
                throw new IllegalArgumentException("Invalid Parameter: " + Arrays.toString(split));
            }
            String[] keyValue = new String[]{"", ""};
            for (int i = 0; i < split.length; i++) {
                keyValue[i] = split[i];

            }
            keyValue[0] = keyValue[0].toLowerCase();
            pairCookies.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return new Cookie(pairCookies);
    }
}
