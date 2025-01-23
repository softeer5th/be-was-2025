package util;

/**
 * 문자열에 포함된 HTML 특수 문자를 HTML 엔티티로 변환하는 유틸리티 클래스
 */
public class HtmlEscapeUtil {
    /**
     * 문자열에 포함된 HTML 특수 문자를 HTML 엔티티로 변환한다.
     *
     * @param input HTML 태그가 포함된 문자열
     * @return HTML 엔티티로 변환된 문자열
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}