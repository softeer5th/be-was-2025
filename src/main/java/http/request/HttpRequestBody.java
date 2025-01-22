package http.request;

import static enums.ContentType.*;
import static enums.HttpHeader.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequestBody {
	private final int MAX_BODY_SIZE = 100 * 1024 * 1024; // 최대 100MB로 제한
	private static final String BOUNDARY_PREFIX = "--";
	private static final String BOUNDARY_DELIMITER = ";";
	private static final String HEADER_SEPARATOR = ":";

	private static final String HEADER_BODY_SEPARATOR = "\r\n\r\n";
	private static final String CRLF = "\r\n";
	private byte[] body;

	public HttpRequestBody(InputStream in, HttpHeaders headers) throws IOException {
		if (headers.containsHeader(CONTENT_LENGTH.getValue().toLowerCase())) {
			List<String> contentLengthValue = headers.getHeader(CONTENT_LENGTH.getValue());

			try {
				int contentLength = Integer.parseInt(contentLengthValue.get(0));
				if (contentLength < 0) {
					throw new IllegalArgumentException("Content-Length must not be negative");
				}

				if (contentLength > MAX_BODY_SIZE) {
					throw new IllegalArgumentException("Content-Length exceeds the maximum allowed size of " + MAX_BODY_SIZE + " bytes");
				}

				this.body = readBody(in, contentLength);

			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid Content-Length value: " + contentLengthValue.get(0), e);
			}
		}
	}

	private byte[] readBody(InputStream in, int contentLength) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[MAX_BODY_SIZE]; // 8KB buffer
		int remaining = contentLength;

		while (remaining > 0) {
			int bytesRead = in.read(buffer, 0, Math.min(buffer.length, remaining));
			if (bytesRead == -1) {
				throw new IOException("Unexpected end of stream");
			}
			outputStream.write(buffer, 0, bytesRead);
			remaining -= bytesRead;
		}

		return outputStream.toByteArray();
	}

	public byte[] getBodyAsByteArray() {
		return body;
	}

	public String getBodyAsString() {
		return new String(body, StandardCharsets.UTF_8);
	}

	public Optional<Map<String, String>> getBodyAsMap() {
		String body = new String(this.body, StandardCharsets.UTF_8);

		// 결과를 저장할 Map 생성
		Map<String, String> resultMap = new HashMap<>();

		// &로 분리하여 각 key=value 쌍을 처리
		String[] pairs = body.split("&");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=", 2); // = 기준으로 분리 (최대 2개로 분리)

			if (keyValue.length >= 1) {
				try {
					String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
					String value = keyValue.length == 2 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()) : "";
					resultMap.put(key, value);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException("Failed to decode URL-encoded data: " + pair, e);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return Optional.ofNullable(resultMap);
	}

	public List<Map<String, String>> getBodyAsMultipart(HttpHeaders headers) {
		// Content-Type 헤더가 없으면 예외 처리
		String contentType = headers.getHeader(CONTENT_TYPE.getValue())
			.stream()
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Missing Content-Type header"));

		// Content-Type이 multipart로 시작하지 않으면 예외 처리
		if (!contentType.contains(MULTIPART_FORM_DATA.getMimeType())) {
			throw new IllegalArgumentException("Content-Type is not multipart");
		}

		// Boundary 값 추출
		String boundary = extractBoundary(contentType);
		if (boundary == null) {
			throw new IllegalArgumentException("Boundary parameter is missing in Content-Type");
		}

		// multipart 형식 본문을 처리
		String bodyString = new String(body, StandardCharsets.ISO_8859_1);
		List<Map<String, String>> parts = new ArrayList<>();

		// Boundary로 본문을 나누고, 각 파트를 처리
		String[] rawParts = bodyString.split(BOUNDARY_PREFIX + boundary);
		for (String rawPart : rawParts) {
			rawPart = rawPart.trim();
			if (rawPart.isEmpty() || rawPart.equals(BOUNDARY_PREFIX)) continue;

			// 파트 본문을 헤더와 본문으로 나눔
			String[] partLines = rawPart.split(HEADER_BODY_SEPARATOR, 2);
			if (partLines.length < 2) continue;

			// 각 파트를 헤더와 본문으로 나누어 저장
			String headersPart = partLines[0];
			String bodyPart = partLines[1].trim();

			Map<String, String> partData = new HashMap<>();
			partData.put("headers", parseHeaders(headersPart).toString());
			partData.put("body", bodyPart);

			parts.add(partData);
		}

		return parts;
	}

	private String extractBoundary(String contentType) {
		String[] params = contentType.split(BOUNDARY_DELIMITER);
		for (String param : params) {
			param = param.trim();
			if (param.startsWith("boundary=")) {
				return param.substring("boundary=".length()).replace("\"", "");
			}
		}
		return null;
	}

	private Map<String, String> parseHeaders(String headersPart) {
		Map<String, String> headers = new HashMap<>();
		String[] lines = headersPart.split(CRLF);
		for (String line : lines) {
			int colonIndex = line.indexOf(HEADER_SEPARATOR);
			if (colonIndex > 0) {
				String key = line.substring(0, colonIndex).trim();
				String value = line.substring(colonIndex + 1).trim();
				headers.put(key, value);
			}
		}
		return headers;
	}

}
