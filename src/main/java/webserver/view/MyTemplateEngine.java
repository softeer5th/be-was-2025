package webserver.view;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTemplateEngine implements TemplateEngine {
    private static final String TAG_PREFIX = "my-";
    private final Map<String, TagRenderer> tagHandlers = new ConcurrentHashMap<>();
    // my- 로 시작하는 여는 태그 및 닫는 태그를 찾기 위한 정규식
    private final Pattern tagPattern = Pattern.compile("<(/?my-[a-z]+)(( *([a-z]+)=\"([a-z0-9.&|!/]+)\")*)>");

    public MyTemplateEngine registerTagHandler(TagRenderer tagRenderer) {
        // 태그 이름이 my-로 시작하는지 확인
        assert tagRenderer.getTagName().startsWith(TAG_PREFIX);
        tagHandlers.put(tagRenderer.getTagName(), tagRenderer);
        tagRenderer.setEngine(this);
        return this;
    }

    // 쌍에 맞게 태그를 파싱하여 알맞는 태그 핸들러에게 전달 후 결과를 순서대로 조합하여 반환
    @Override
    public String render(String template, Map<String, Object> model) {
        StringBuilder rendered = new StringBuilder();
        List<TagMatchingResult> matchingResults = matchTag(template);
        // plain html은 그대로 반환
        if (matchingResults.isEmpty()) {
            return template;
        }
        for (int i = 0; i < matchingResults.size(); i++) {
            TagMatchingResult result = matchingResults.get(i);
            TagRenderer tagRenderer = tagHandlers.get(result.outerTagName);
            String childrenTemplate = extractChildrenTemplate(template, result);
            // plain html 부분을 렌더링 결과에 추가
            if (i == 0)
                rendered.append(template, 0, result.firstTagStart);
            else
                rendered.append(template, matchingResults.get(i - 1).lastTagEnd, result.firstTagStart);
            // 태그를 렌더링하여 렌더링 결과에 추가
            rendered.append(tagRenderer.handle(model, result.outerTagAttributes, childrenTemplate));

            // plain html 부분을 렌더링 결과에 추가
            if (i == matchingResults.size() - 1)
                rendered.append(template, result.lastTagEnd, template.length());

        }
        return rendered.toString();
    }

    // 매칭된 태그 쌍의 자식 요소를 문자열로 추출
    private String extractChildrenTemplate(String template, TagMatchingResult result) {
        int openTagEnd = template.indexOf(">", result.firstTagStart) + 1;
        int closeTagStart = template.lastIndexOf("</" + result.outerTagName, result.lastTagEnd);
        return template.substring(openTagEnd, closeTagStart).strip();
    }

    // 매칭되는 태그 쌍 찾기
    private List<TagMatchingResult> matchTag(String template) {
        List<TagMatchingResult> results = new ArrayList<>();
        Matcher matcher = tagPattern.matcher(template);
        // 태그 쌍을 찾기 위한 스택
        Stack<String> tagStack = new Stack<>();

        boolean outerTagFound = false;
        int outerTagStart = -1;
        int outerTagEnd = -1;

        String outerTagName = null;
        Map<String, String> outerTagAttributes = null;
        while (matcher.find()) {
            String tagName = matcher.group(1);
            if (!outerTagFound) {
                // fist open tag
                outerTagName = tagName;
                outerTagStart = matcher.start(0);
                outerTagAttributes = parseAttributes(matcher.group(2));
                outerTagFound = true;
            }
            if (tagName.startsWith("/")) {
                String startTag = tagStack.pop();
                // 여닫는 태그 쌍이 맞지 않으면 예외 발생
                if (!startTag.equals(tagName.substring(1))) {
                    throw new IllegalArgumentException("Invalid template");
                }
                if (tagStack.isEmpty()) {
                    // last close tag
                    outerTagEnd = matcher.end(0);
                    results.add(new TagMatchingResult(outerTagStart, outerTagEnd, outerTagName, outerTagAttributes));
                    outerTagFound = false;
                    outerTagName = null;
                    outerTagStart = -1;
                    outerTagEnd = -1;
                    outerTagAttributes = null;
                }
            } else {
                tagStack.push(tagName);
            }
        }
        return results;
    }

    // 태그의 속성을 파싱하여 Map으로 반환
    private Map<String, String> parseAttributes(String attributes) {
        Map<String, String> result = new HashMap<>();
        String[] split = attributes.split(" ");
        for (String s : split) {
            if (s.isBlank()) {
                continue;
            }
            String[] keyValue = s.split("=");
            result.put(keyValue[0], keyValue[1].substring(1, keyValue[1].length() - 1));
        }
        return result;
    }

    // 매칭된 태그 쌍을 저장하는 클래스
    private record TagMatchingResult(
            int firstTagStart, // 여는 태그 시작 인덱스
            int lastTagEnd,  // 닫는 태그 끝 인덱스
            String outerTagName, // 태그 이름
            Map<String, String> outerTagAttributes // 태그 속성
    ) {

    }


}
