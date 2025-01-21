package view;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import http.HttpSession;
import http.response.HttpResponse;

public class TemplateEngine {

	// TODO : template 의존성 고민
	public void render(HttpResponse response, HttpSession model) {
		if(!response.hasToRender()) {
			return;
		}

		String template = response.getBodyToString();
		template = processSongIf(template, model);

		if(model == null) {
			response.setBody(template.getBytes());
			return;
		}

		for (Map.Entry<String, Object> entry : model.getValues().entrySet()) {
			template = template.replace("{" + entry.getKey() + "}", entry.getValue().toString());
		}

		response.setBody(template.getBytes());
	}

	private String processSongIf(String template, HttpSession model) {
		// 정규식 패턴 정의
		Pattern ifPattern = Pattern.compile("<song:if condition=\"(.*?)\">(.*?)</song:if>", Pattern.DOTALL);
		Matcher ifMatcher = ifPattern.matcher(template);

		StringBuilder result = new StringBuilder();

		// <song:if> 태그를 하나씩 처리
		while (ifMatcher.find()) {
			String condition = ifMatcher.group(1); // condition 속성
			String ifContent = ifMatcher.group(2); // 태그 안의 내용

			// 조건 평가
			boolean isTrue = evaluateCondition(condition, model);

			// 조건에 따라 내용 치환
			if (isTrue) {
				ifMatcher.appendReplacement(result, Matcher.quoteReplacement(ifContent));
			} else {
				ifMatcher.appendReplacement(result, "");
			}
		}

		// 나머지 문자열 추가
		ifMatcher.appendTail(result);

		return result.toString();
	}

	private boolean evaluateCondition(String condition, HttpSession model) {
		// 간단한 조건 해석 로직
		if (condition.equals("session.user")) {
			return model != null && model.getAttribute("name") != null;
		}
		if (condition.equals("!session.user")) {
			return !(model != null && model.getAttribute("name") != null);
		}
		return false;
	}
}