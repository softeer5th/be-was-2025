package util;

import constant.HTTPCode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class UtilsTest {

    @Test
    public void testIsValidHttpMethod() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        boolean resultValid = Utils.isValidHttpMethod("GET", dos);
        boolean resultInvalid = Utils.isValidHttpMethod("INVALID_METHOD", dos);

        assertThat(resultValid).isTrue();
        assertThat(resultInvalid).isFalse();
    }

    @Test
    public void testIsValidHeader() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        String[] validHeader = {"GET", "/index.html", "HTTP/1.1"};
        String[] invalidHeader1 = {"GET", "/index.html"};
        String[] invalidHeader2 = {"GET", "/index.html", "HTTP/1.1", "hi"};

        boolean resultValid = Utils.isValidHeader(validHeader, dos);
        boolean resultInvalid1 = Utils.isValidHeader(invalidHeader1, dos);
        boolean resultInvalid2 = Utils.isValidHeader(invalidHeader2, dos);

        assertThat(resultValid).isTrue();
        assertThat(resultInvalid1).isFalse();
        assertThat(resultInvalid2).isFalse();
    }

    @Test
    public void testRemoveLastSlash() {
        String resultWithSlash = Utils.removeLastSlash("/index.html/");
        String resultWithoutSlash = Utils.removeLastSlash("/index.html");
        String resultRoot = Utils.removeLastSlash("/");

        assertThat(resultWithSlash).isEqualTo("/index.html");
        assertThat(resultWithoutSlash).isEqualTo("/index.html");
        assertThat(resultRoot).isEqualTo("");
    }

    @Test
    public void testReadInputToArray() {
        String input = "GET /index.html HTTP/1.1\nHost: localhost\n\n";
        InputStream in = new java.io.ByteArrayInputStream(input.getBytes());

        try {
            String[] result = Utils.readInputToArray(in);
            assertThat(result).containsExactly("GET /index.html HTTP/1.1", "Host: localhost");
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        }
    }

    @Test
    public void testFileToByteArray() {
        java.io.File file = new java.io.File("src/test/resources/test-file.txt");
        file.getParentFile().mkdirs();
        try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
            writer.write("Hello, world!");
        } catch (Exception e) {
            fail("Failed to create test file: " + e.getMessage());
        }

        byte[] fileBytes = Utils.fileToByteArray(file);
        assertThat(fileBytes).isNotNull();
        assertThat(new String(fileBytes)).isEqualTo("Hello, world!");

        file.delete();
    }
}