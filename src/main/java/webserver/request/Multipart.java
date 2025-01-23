package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import webserver.enums.ContentType;
import webserver.enums.HttpHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Multipart {

    private static final Logger log = LoggerFactory.getLogger(Multipart.class);
    private final Map<String, FormData> formData;

    public Multipart(List<FormData> formData) {
        this.formData = new HashMap<>();
        for (FormData data : formData) {
            this.formData.put(data.name, data);
        }
    }


    /**
     * multipart/form-data로 전송된 데이터를 문자열로 반환한다.
     *
     * @param name form data name
     * @return 문자열로 변환된 데이터. 없다면 null
     */
    public String getString(String name) {
        if (!has(name)) {
            return null;
        }
        return new String(formData.get(name).body).strip();
    }

    /**
     * multipart/form-data로 전송된 데이터를 boolean으로 반환한다.
     *
     * @param name form data name
     * @return boolean으로 변환된 데이터. 없다면 null
     */
    public Boolean getBoolean(String name) {
        if (!has(name)) {
            return null;
        }
        return Boolean.parseBoolean(getString(name));
    }

    /**
     * multipart/form-data로 전송된 데이터에 name이 존재하는지 확인한다.
     *
     * @param name form data name
     * @return name이 존재하는지 여부
     */
    public boolean has(String name) {
        return formData.containsKey(name);
    }

    public String saveFile(String name, FileUploader fileUploader) {
        validateFile(name);
        FormData data = formData.get(name);
        String filename = data.filename;
        if (data.contentType.fileExtension != null &&
            !filename.endsWith(data.contentType.fileExtension)) {
            filename += "." + data.contentType.fileExtension;
        }
        log.info("upload file request: filename: {}, content type: {}", data.filename, data.contentType.mimeType);
        return fileUploader.uploadFile(filename, data.body);
    }

    private void validateFile(String name) {
        FormData data = formData.get(name);
        if (data == null || !data.isFile()) {
            throw new IllegalArgumentException("파일이 아닌 데이터입니다.");
        }
    }

    static class FormData {
        private final String name;
        private final String filename;
        private final ContentType contentType;
        private byte[] body;

        public FormData(Map<String, String> headers, Map<String, String> attributes, byte[] body) {
            this.name = attributes.get("name");
            this.filename = attributes.get("filename");
            if (headers.containsKey(HttpHeader.CONTENT_TYPE.value)) {
                this.contentType = ContentType.fromMimeType(headers.get(HttpHeader.CONTENT_TYPE.value));
            } else if (filename != null) {
                this.contentType = ContentType.fromExtension(FileUtil.getFileExtension(filename));
            } else {
                this.contentType = ContentType.APPLICATION_OCTET_STREAM;
            }
            this.body = body;
        }


        public boolean isFile() {
            return filename != null;
        }
    }

}
