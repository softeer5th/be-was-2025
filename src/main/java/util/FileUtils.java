package util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {

	public static final int BUFFER_SIZE = 8192; // 8KB 버퍼 크기, 보통 디스크 페이지 크기는 8KB 이다.

	public static String getFileAsString(String filePath) throws IOException {
		byte[] fileBytes = getFileAsByteArray(filePath);
		// UTF-8 인코딩으로 byte 배열을 String으로 변환
		return new String(fileBytes, StandardCharsets.UTF_8);
	}

	// 파일을 byte[]로 반환하는 메서드
	// 일정 크기의 버퍼를 두어, 데이터를 여러 바이트씩 한 번에 읽어오는 방식으로 성능을 최적화
	public static byte[] getFileAsByteArray(String filePath) throws IOException {
		try (InputStream inputStream = getFileAsInputStream(filePath);
			 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}
			return byteArrayOutputStream.toByteArray();
		}
	}

	private static InputStream getFileAsInputStream(String filePath) throws IOException {
		InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);
		if (inputStream == null) {
			throw new FileNotFoundException("File not found: " + filePath);
		}
		return inputStream;
	}
}
