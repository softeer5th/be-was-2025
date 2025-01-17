package http.cookie;

import java.util.List;

public class Cookie {
    private String name;
    private String value;
    private boolean toDiscard;
    private String domain;
    private long maxAge = MAX_AGE_UNSPECIFIED;
    private String path;
    private SameSite sameSite;
    private boolean secure;
    private boolean httpOnly;

    private final long whenCreated;
    private static final long MAX_AGE_UNSPECIFIED = -1;

    public enum SameSite{
        STRICT, LAX, NONE
    };


    public Cookie(String name, String value) {
        this(name, value, System.currentTimeMillis());
    }

    public Cookie(String name, String value, long creationTime){
        name = name.trim();

        // 일반 사용자가 정한 쿠키 이름은 $로 시작하지 않아야 한다. $이 시스템 정의하거 예약된 속성이다.
        if(name.isEmpty() || !isToken(name) || name.charAt(0) == '$'){
            throw new IllegalArgumentException("Illegal cookie name");
        }

        this.name = name;
        this.value = value;
        toDiscard = false;
        secure = false;
        whenCreated = creationTime;
    }


    public boolean hasExpired(){
        if(maxAge == 0) return true;

        if(maxAge < 0) return false;

        long deltaSecond = (System.currentTimeMillis() - whenCreated) / 1000;
        if(deltaSecond > maxAge)
            return true;
        else
            return false;
    }

    public boolean getDiscard() { return toDiscard;}

    public void setDomain(String pattern){
        if(pattern != null)
            domain = pattern.toLowerCase();
        else
            domain = pattern;
    }

    public String getDomain() { return domain; }

    public void setMaxAge(long expiry) { maxAge = expiry; }

    public long getMaxAge() { return maxAge; }

    public void setPath(String uri) { path = uri; }

    public String getPath() { return path; }

    public void setSameSite(SameSite sameSite) { this.sameSite = sameSite;}

    public SameSite getSameSite() { return sameSite; }

    public void setSecure(boolean flag) { secure = flag; }

    public boolean getSecure() { return secure; }

    public String getName() { return name; }

    public void setValue(String newValue) { value = newValue; }

    public String getValue() { return value; }

    public boolean isHttpOnly() { return httpOnly; }

    public void setHttpOnly(boolean httpOnly) { this.httpOnly = httpOnly; }


    public static Cookie parseCookie(String cookieValue){
        String[] cookieValueParts = cookieValue.trim().split("=");

        if(cookieValueParts.length != 2){
            throw new IllegalArgumentException("Invalid cookie format");
        }

        return new Cookie(cookieValueParts[0], cookieValueParts[1]);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getName()).append("=").append(getValue());

        if(getDomain() != null)
            sb.append("; Domain=").append(getDomain());

        if(httpOnly){
            sb.append("; HttpOnly");
        }

        if(maxAge != MAX_AGE_UNSPECIFIED){
            sb.append("; Max-Age=").append(maxAge);
        }

        if(getPath() != null){
            sb.append("; Path=").append(getPath());
        }

        if(sameSite != null){
            sb.append("; SameSite=").append(getSameSite().name());
        }

        if(secure){
            sb.append("; Secure");
        }

        return sb.toString();
    }

    private static final String tspecials = ",; ";

    private static boolean isToken(String value) {
        int len = value.length();

        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);

            // 0x20 미만 문(공백 전 ASCII 문자), 0x7f 이상 문(DEL 문자 이상 ASCII 문자)
            if (c < 0x20 || c >= 0x7f || tspecials.indexOf(c) != -1)
                return false;
        }
        return true;
    }

}
