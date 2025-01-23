package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * The type Stream util.
 */
public class StreamUtil {

	/**
	 * Read until crlf as string string.
	 *
	 * @param inputStream the input stream
	 * @return the string
	 * @throws IOException the io exception
	 */
	public static String readUntilCRLFAsString(InputStream inputStream) throws IOException {
		return new String(readUntilCRLF(inputStream), StandardCharsets.UTF_8);
	}

	private static byte[] readUntilCRLF(InputStream inputStream) throws IOException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			int prevByte = -1;
			int currentByte;

			while ((currentByte = inputStream.read()) != -1) {
				buffer.write(currentByte);
				if (prevByte == '\r' && currentByte == '\n') {
					break;
				}
				prevByte = currentByte;
			}

			return buffer.toByteArray();
		}
	}
}
