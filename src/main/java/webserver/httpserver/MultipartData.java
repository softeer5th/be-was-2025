package webserver.httpserver;

import exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.header.ContentDisposition;
import webserver.httpserver.header.MimeType;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MultipartData {
    public static final String HEADER_KEY_VALUE_DELIMITER = ":";
    public static final String CONTENT_DISPOSITION = "content-disposition";
    public static final String CONTENT_TYPE = "content-type";
    private static final Logger log = LoggerFactory.getLogger(MultipartData.class);
    private final Map<String, String> headers = new HashMap<>();
    private ContentDisposition contentDisposition;
    private MimeType mimeType;
    private String name;
    private byte[] body;

    public MultipartData(byte[] header, byte[] body) {
        String[] headerStrings = new String(header).split("\r\n");
        for (String headerString : headerStrings) {
            String[] headerEntry = headerString.split(HEADER_KEY_VALUE_DELIMITER);
            if (headerEntry.length != 2) {
                throw new BadRequestException("헤더가 키-밸류 형태로 오지 않음");
            }
            headers.put(headerEntry[0].trim().toLowerCase(), headerEntry[1].trim());

            log.info("멀티파트 헤더\n key: {} \n value: {}\n",headerEntry[0].trim().toLowerCase(), headerEntry[1].trim());
        }
        if (headers.get(CONTENT_DISPOSITION) == null) {
            throw new BadRequestException("Content-Disposition 헤더가 도착하지 않음");
        }

        contentDisposition = new ContentDisposition(headers.get(CONTENT_DISPOSITION));
        name = contentDisposition.getName().orElseThrow(()->new BadRequestException("name 속성이 도착하지 않음"));
        mimeType = new MimeType(headers.getOrDefault(CONTENT_TYPE, ""));
        this.body = body;
    }

    public ContentDisposition getContentDisposition() {
        return contentDisposition;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public String getName() {
        return name;
    }

    public byte[] getBody() {
        return body;
    }
}
