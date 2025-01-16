package utils;

import java.io.*;

public class FileUtils {
    /**
     * 주어진 파일을 byte 배열로 읽는 메소드
     * @param file
     * @return 읽은 파일의 byte 배열
     * @throws IOException 존재하지 않는 파일일 경우 or 파일을 읽는 도중 실패한 경우 or 읽기 권한이 없는 파일일 경우
     */
    public static byte[] getFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        }
    }

    /**
     * 주어진 파일을 문자열로 읽는 메소드.
     * @param file
     * @return 파일을 문자열로 읽은 값 반환
     * @throws IOException 존재하지 않는 파일일 경우 or 파일을 읽는 도중 실패한 경우 or 읽기 권한이 없는 파일일 경우
     */
    public static String getFileAsString(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            return builder.toString();
        }
    }
}
