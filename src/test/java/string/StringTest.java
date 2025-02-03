package string;

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringTest {


	@Test
	@DisplayName("특정 데이터에 대해서 UTF-8 변환 방식은 데이터가 손상될 수 있다.")
	void stringTest(){
		// given
		byte[] binaryData = new byte[]{
			(byte) 0x89, 0x50, 0x4E, 0x47, // PNG header (\x89PNG)
			(byte) 0x0D, (byte) 0x0A, // \r\n
			(byte) 0x1A, 0x0A // Binary values
		};

		// when
		String utf8_string = new String(binaryData, StandardCharsets.UTF_8);
		String iso8859_string = new String(binaryData, StandardCharsets.ISO_8859_1);

		// then
		Assertions.assertThat(utf8_string).isNotEqualTo(iso8859_string);
	}
}
