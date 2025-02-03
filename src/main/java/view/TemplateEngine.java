package view;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import http.response.HttpResponse;

public class TemplateEngine {

	public static void render(HttpResponse response) {
		if (response.getView() == null) {
			return;
		}

		View view = response.getView();

		String template = response.getBodyToString();
		template = processSongIf(template, view);

		template = resolvePlaceholders(template, view);

		response.setBody(template.getBytes());
	}

	private static String processSongIf(String template, View view) {
		// 정규식 패턴 정의
		Pattern ifPattern = Pattern.compile("<song:if condition=\"(.*?)\">(.*?)</song:if>", Pattern.DOTALL);
		Matcher ifMatcher = ifPattern.matcher(template);

		StringBuilder result = new StringBuilder();

		// <song:if> 태그를 하나씩 처리
		while (ifMatcher.find()) {
			String condition = ifMatcher.group(1); // condition 속성
			String ifContent = ifMatcher.group(2); // 태그 안의 내용

			// 조건 평가
			boolean isTrue = evaluateCondition(condition, view);

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

	private static boolean evaluateCondition(String condition, View view){
		// 간단한 조건 해석 로직

		boolean returnValue = false;

		if (condition.contains("user")) {
			returnValue = (view != null && view.getAttribute("user").isPresent());
		}

		if (condition.contains("hasPrevPage")) {
			Optional<Object> attribute = view.getAttribute("hasPrevPage");
			returnValue = (boolean) attribute.orElse(false);
		}
		if (condition.contains("hasNextPage")) {
			Optional<Object> attribute = view.getAttribute("hasNextPage");
			returnValue = (boolean) attribute.orElse(false);
		}

		if (condition.contains("!")) {
			returnValue = !returnValue;
		}

		return returnValue;
	}

	private static String resolvePlaceholders(String input, View view) {
		Pattern pattern = Pattern.compile("\\{([^}]+)}");
		Matcher matcher = pattern.matcher(input);

		// 결과를 저장할 StringBuilder
		StringBuilder result = new StringBuilder();

		try {
			while (matcher.find()) {
				String placeHolder = matcher.group(1); // 중괄호 안의 내용
				String[] keyValue = placeHolder.split("\\.");
				String key = keyValue[0];
				String fieldName = (keyValue.length > 1) ? keyValue[1] : placeHolder;

				Optional<Object> attribute = view.getAttribute(key);
				if (attribute.isPresent()) {
					Object object = attribute.get();

					if (keyValue.length > 1) {
						// 점(.)으로 구분된 경우
						Field declaredField = object.getClass().getDeclaredField(fieldName);
						declaredField.setAccessible(true);
						Object fieldValue = declaredField.get(object); // 필드의 실제 값 가져오기
						matcher.appendReplacement(result, Matcher.quoteReplacement(String.valueOf(fieldValue))); // 값을 치환
					} else {
						// 점(.)이 없는 단일 key의 경우
						matcher.appendReplacement(result, Matcher.quoteReplacement(String.valueOf(attribute.get())));
					}
				}

			}
			matcher.appendTail(result); // 나머지 문자열 추가

			return result.toString();
		} catch (NoSuchFieldException e) {
			throw new InternalError("해당 필드가 존재하지 않습니다.");
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}