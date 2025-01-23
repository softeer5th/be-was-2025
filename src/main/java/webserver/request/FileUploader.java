package webserver.request;

/**
 * 파일 업로드를 담당하는 인터페이스
 */
public interface FileUploader {
    /**
     * 파일을 업로드한다.
     *
     * @param filename 파일 이름
     * @param body     파일 내용
     * @return 업로드된 파일의 url path.
     */
    String uploadFile(String filename, byte[] body);

    /**
     * 업로드된 파일을 삭제한다.
     *
     * @param path 삭제할 파일의 url path.
     */
    void deleteFile(String path);
}
