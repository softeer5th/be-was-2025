package webserver.httpserver.header;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {
    public static final Cookie NULL_COOKIE = new NullCookie();
    private final Map<String, String> pairCookies = new HashMap<>();
    private String domain;
    private String path;
    private Integer maxAge;
    private LocalDateTime expires;
    private boolean secure;
    private boolean httpOnly;
    private String sameSite;

    public Cookie() {
    }

    public Cookie(String values) {
        String[] cookieParts = values.split(";");
        for (String cookiePart : cookieParts) {
            String[] keyValue = cookiePart.split("=");
            keyValue[0] = keyValue[0].toLowerCase();
            if ("domain".equals(keyValue[0])){
                domain = keyValue[1].trim();
            }
            else if ("path".equals(keyValue[0])){
                path = keyValue[1].trim();
            }
            else if ("max-age".equals(keyValue[0])){
                maxAge = Integer.parseInt(keyValue[1].trim());
            }
            else if ("secure".equals(keyValue[0])){
                secure = true;
            }
            else if ("httpOnly".equals(keyValue[0])){
                httpOnly = true;
            }
            else if ("sameSite".equals(keyValue[0])){
                sameSite = keyValue[1].trim();
            } else if (keyValue.length == 2) {
                pairCookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
    }

    public void setValue(String key, String value) {
        pairCookies.put(key, value);
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }

    public String getCookie(String key) {
        return pairCookies.getOrDefault(key, "false");
    }



    /**
     * 쿠키를 문자열로 바꾸는 함수
     * pairCookie 가 비어있을 경우, 정상적으로 join되는지 확인 필요 -> 안됨
     * 스트림으로 이어서 직접 만들기
     * @return 쿠키의 밸류값
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String keyValuePair = pairCookies.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("; "));
        builder.append(keyValuePair);
        if (domain != null) {
            builder.append("; Domain=").append(domain);
        }
        if (path != null) {
            builder.append("; Path=").append(path);
        }
        if (maxAge != null) {
            builder.append("; Max-Age=").append(maxAge);
        }
        if (expires != null) {
            builder.append("; Expires=").append(expires);
        }
        if (secure) {
            builder.append("; Secure");
        }
        if (httpOnly) {
            builder.append("; HttpOnly");
        }
        if (sameSite != null) {
            builder.append("; SameSite=").append(sameSite);
        }
        return builder.toString();
    }


    private static class NullCookie extends Cookie {
        private NullCookie() {
        }
    }
}
