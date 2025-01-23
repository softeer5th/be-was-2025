package webserver.view;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 커스텀 html 태그 기반의 템플릿을 렌더링하는 엔진
 * 각 커스텀 태그를 처리하는 TagRenderer를 등록하여 사용한다.
 * 엔진은 템플릿 문자열을 파싱하여 커스텀 태그를 찾고, 각 태그의 렌더링은 TagRenderer에 위임하여 이를 취합하여 최종 결과를 만든다.
 */
public class MyTemplateEngine implements TemplateEngine {

    // my- 로 시작하는 여는 태그 및 닫는 태그를 찾기 위한 정규식
    private static final Pattern TAG_PATTERN = Pattern.compile("<(/?my-[a-z]+)((?: *\\w+=\"[^\"]+\")*)>");
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(" *(\\w+)=\"([^\"]+)\"");
    private static final String TAG_NAME_PREFIX = "my-";
    private static final String CLOSE_TAG_NAME_PREFIX = "/";

    private final Map<String, TagRenderer> tagHandlers = new ConcurrentHashMap<>();

    /**
     * 각 커스텀 태그를 렌더링하는 TagRenderer를 등록한다.
     *
     * @param tagRenderer 커스텀 태그를 렌더링하는 핸들러
     * @return this
     */
    public MyTemplateEngine registerTagHandler(TagRenderer tagRenderer) {
        // 태그 이름이 my-로 시작하는지 확인
        assert tagRenderer.getTagName().startsWith(TAG_NAME_PREFIX);
        tagHandlers.put(tagRenderer.getTagName(), tagRenderer);
        tagRenderer.setEngine(this);
        return this;
    }

    /**
     * 템플릿 문자열을 렌더링한다.
     *
     * @param template 템플릿 문자열
     * @param model    렌더링에 사용할 데이터
     * @return 렌더링 결과 html
     */
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
            TagRenderer tagRenderer = tagHandlers.get(result.tagName);
            String childrenTemplate = result.childTemplate;

            // plain html 부분을 렌더링 결과에 추가
            int start = i == 0 ? 0 : matchingResults.get(i - 1).lastTagEnd;
            rendered.append(template, start, result.firstTagStart);

            // 태그를 렌더링하여 렌더링 결과에 추가
            rendered.append(tagRenderer.handle(model, result.tagAttributes, childrenTemplate).strip());
        }
        // 남아있는 plain html 부분을 렌더링 결과에 추가
        rendered.append(template, matchingResults.get(matchingResults.size() - 1).lastTagEnd, template.length());
        return rendered.toString().strip();
    }

    /**
     * 템플릿 문자열에서 커스텀 태그 쌍을 찾아 반환한다.
     *
     * @param template 템플릿 문자열
     * @return 커스텀 태그 쌍 목록
     */
    // 매칭되는 태그 쌍 찾기
    private List<TagMatchingResult> matchTag(String template) {
        List<TagMatchingResult> matchingResults = new ArrayList<>();
        Matcher matcher = TAG_PATTERN.matcher(template);
        // 태그 쌍을 찾기 위한 스택
        Stack<String> openTagStack = new Stack<>();

        boolean isCurrentTagFound = false;
        int currentTagStart = -1;
        int currentTagEnd = -1;
        int currentOpenTagEnd = -1;
        int currentCloseTagStart = -1;

        String currentTagName = null;
        Map<String, String> currentTagAttributes = null;

        while (matcher.find()) {
            String tagName = matcher.group(1);
            if (!isCurrentTagFound) {
                // fist open tag
                currentTagName = tagName;
                currentTagStart = matcher.start(0);
                currentTagAttributes = parseAttributes(matcher.group(2));
                currentOpenTagEnd = matcher.end(0);
                isCurrentTagFound = true;
            }
            if (tagName.startsWith(CLOSE_TAG_NAME_PREFIX)) {
                String startTag = openTagStack.pop();
                // 여닫는 태그 쌍이 맞지 않으면 예외 발생
                if (!startTag.equals(tagName.substring(1))) {
                    throw new IllegalArgumentException("Invalid template");
                }
                if (openTagStack.isEmpty()) {
                    // last close tag
                    currentTagEnd = matcher.end(0);
                    currentCloseTagStart = matcher.start(0);
                    String childTemplate = template.substring(currentOpenTagEnd, currentCloseTagStart);
                    matchingResults.add(new TagMatchingResult(currentTagStart, currentTagEnd, currentTagName, currentTagAttributes, childTemplate));
                    isCurrentTagFound = false;
                }
            } else {
                openTagStack.push(tagName);
            }
        }
        return matchingResults;
    }

    /**
     * 태그의 속성을 파싱하여 Map으로 반환한다.
     *
     * @param attributes 속성 문자열. ex) "key1="value1" key2="value2""
     * @return 속성 이름과 값의 맵
     */
    // 태그의 속성을 파싱하여 Map으로 반환
    private Map<String, String> parseAttributes(String attributes) {
        Map<String, String> result = new HashMap<>();
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(attributes);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            result.put(key, value);
        }
        return result;
    }

    /**
     * 매칭된 태그 쌍을 저장하는 클래스
     *
     * @param firstTagStart 여는 태그 시작 인덱스
     * @param lastTagEnd    닫는 태그 끝 인덱스
     * @param tagName       태그 이름
     * @param tagAttributes 태그 속성 이름과 값을 담은 Map
     * @param childTemplate 태그 안쪽의 자식 템플릿 문자열. 또 다른 커스텀 태그를 포함할 수 있음
     */
    private record TagMatchingResult(
            int firstTagStart,
            int lastTagEnd,
            String tagName,
            Map<String, String> tagAttributes,
            String childTemplate
    ) {

    }


}
