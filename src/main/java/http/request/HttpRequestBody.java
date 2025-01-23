package http.request;

import static enums.HttpHeader.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequestBody {
	private final int MAX_BODY_SIZE = 100 * 1024 * 1024; // 최대 100MB로 제한
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
}
