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

	public List<Map<String, Object>> getBodyAsMultipart(HttpHeaders headers) {
		// Content-Type 헤더에서 boundary 추출
		String contentType = headers.getHeader(CONTENT_TYPE.getValue())
			.stream()
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Missing Content-Type header"));

		if (!contentType.contains(MULTIPART_FORM_DATA.getMimeType())) {
			throw new IllegalArgumentException("Content-Type is not multipart");
		}

		String boundary = extractBoundary(contentType);
		if (boundary == null) {
			throw new IllegalArgumentException("Boundary parameter is missing in Content-Type");
		}

		// boundary로 본문을 나누기
		List<Map<String, Object>> parts = new ArrayList<>();
		byte[] boundaryBytes = (BOUNDARY_PREFIX + boundary).getBytes(StandardCharsets.ISO_8859_1);
		byte[] bodyCopy = this.body; // 원본 유지

		int start = 0;
		while (start < bodyCopy.length) {
			int boundaryIndex = findBoundary(bodyCopy, start, boundaryBytes);
			if (boundaryIndex == -1) break;

			// boundary 사이의 파트를 추출
			int nextPartIndex = findBoundary(bodyCopy, boundaryIndex + boundaryBytes.length, boundaryBytes);
			if (nextPartIndex == -1) break;

			byte[] partBytes = extractBytes(bodyCopy, boundaryIndex + boundaryBytes.length, nextPartIndex);
			Map<String, Object> part = parseMultipartPart(partBytes);
			if (!part.isEmpty()) {
				parts.add(part);
			}

			start = nextPartIndex;
		}

		return parts;
	}

	// boundary 위치 찾기
	private int findBoundary(byte[] body, int start, byte[] boundary) {
		for (int i = start; i <= body.length - boundary.length; i++) {
			boolean match = true;
			for (int j = 0; j < boundary.length; j++) {
				if (body[i + j] != boundary[j]) {
					match = false;
					break;
				}
			}
			if (match) {
				return i;
			}
		}
		return -1;
	}

	// 배열의 부분 추출
	private byte[] extractBytes(byte[] body, int start, int end) {
		byte[] result = new byte[end - start];
		System.arraycopy(body, start, result, 0, result.length);
		return result;
	}

	// 파트의 헤더와 바디 분리 및 처리
	private Map<String, Object> parseMultipartPart(byte[] partBytes) {
		Map<String, Object> part = new HashMap<>();
		int headerEndIndex = findHeaderBodySeparator(partBytes);
		if (headerEndIndex == -1) return part;

		byte[] headerBytes = extractBytes(partBytes, 0, headerEndIndex);
		byte[] bodyBytes = extractBytes(partBytes, headerEndIndex + HEADER_BODY_SEPARATOR.length(), partBytes.length);

		Map<String, String> headers = parseHeaders(new String(headerBytes, StandardCharsets.ISO_8859_1));
		part.put("headers", headers);

		if (headers.containsKey("Content-Disposition") && headers.get("Content-Disposition").contains("filename=")) {
			// 파일 데이터로 저장
			part.put("body", bodyBytes);
		} else {
			// 일반 텍스트로 처리
			part.put("body", new String(bodyBytes, StandardCharsets.UTF_8));
		}

		return part;
	}

	// 헤더와 본문 구분자 찾기
	private int findHeaderBodySeparator(byte[] partBytes) {
		byte[] separatorBytes = HEADER_BODY_SEPARATOR.getBytes(StandardCharsets.ISO_8859_1);
		for (int i = 0; i <= partBytes.length - separatorBytes.length; i++) {
			boolean match = true;
			for (int j = 0; j < separatorBytes.length; j++) {
				if (partBytes[i + j] != separatorBytes[j]) {
					match = false;
					break;
				}
			}
			if (match) {
				return i;
			}
		}
		return -1;
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
