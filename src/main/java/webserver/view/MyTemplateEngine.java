package webserver.view;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTemplateEngine implements TemplateEngine {
    private final Map<String, TagHandler> tagHandlers = new HashMap<>();
    // TODO: 정규표현식에서 "my-" 부분을 규칙을 명확하게 명시하거나, 동적으로 정규표현식을 변경하도록 수정 필요. 하드코딩!!
    private final Pattern tagPattern = Pattern.compile("<(/?my-[a-z]+)(( *([a-z]+)=\"([a-z0-9.&|!]+)\")*)>");

    @Override
    public void registerTagHandler(TagHandler tagHandler) {

        tagHandlers.put(tagHandler.getTagName(), tagHandler);
        tagHandler.setEngine(this);
    }

    @Override
    public String render(String template, Map<String, Object> model) {
        StringBuilder rendered = new StringBuilder();
        List<TagMatchingResult> matchingResults = matchTag(template);
        if (matchingResults.isEmpty()) {
            return template;
        }
        for (int i = 0; i < matchingResults.size(); i++) {
            TagMatchingResult matching = matchingResults.get(i);
            TagHandler tagHandler = tagHandlers.get(matching.outerTagName);
            int openTagEnd = template.indexOf(">", matching.firstTagStart) + 1;
            int closeTagStart = template.lastIndexOf("</" + matching.outerTagName, matching.lastTagEnd);
            String children = template.substring(openTagEnd, closeTagStart).strip();
            if (i == 0)
                rendered.append(template, 0, matching.firstTagStart);
            else
                rendered.append(template, matchingResults.get(i - 1).lastTagEnd, matching.firstTagStart);

            rendered.append(tagHandler.handle(model, matching.outerTagAttributes, children));

            if (i == matchingResults.size() - 1)
                rendered.append(template, matching.lastTagEnd, template.length());

        }
        return rendered.toString();
    }

    // 매칭되는 태그 쌍 찾기
    private List<TagMatchingResult> matchTag(String template) {
        List<TagMatchingResult> results = new ArrayList<>();
        Matcher matcher = tagPattern.matcher(template);
        Stack<String> tagStack = new Stack<>();

        boolean outerTagFound = false;
        int outerTagStart = -1;
        int outerTagEnd = -1;

        String outerTagName = null;
        Map<String, String> outerTagAttributes = null;
        while (matcher.find()) {
            String tagName = matcher.group(1);
            if (!outerTagFound) {
                // first tag
                outerTagName = tagName;
                outerTagStart = matcher.start(0);
                outerTagAttributes = parseAttributes(matcher.group(2));
                outerTagFound = true;
            }
            if (tagName.startsWith("/")) {
                String startTag = tagStack.pop();
                if (!startTag.equals(tagName.substring(1))) {
                    throw new IllegalArgumentException("Invalid template");
                }
                if (tagStack.isEmpty()) {
                    // last tag
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

    private record TagMatchingResult(int firstTagStart, int lastTagEnd, String outerTagName,
                                     Map<String, String> outerTagAttributes) {

    }


}
