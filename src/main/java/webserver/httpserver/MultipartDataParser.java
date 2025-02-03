package webserver.httpserver;

import exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.header.MimeType;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static webserver.httpserver.ContentType.MULTIPART_FORM_DATA;

public class MultipartDataParser {

    public static final String BOUNDARY = "boundary";
    private static final Logger log = LoggerFactory.getLogger(MultipartDataParser.class);

    public static List<MultipartData> parse(HttpRequest request) {
        if (!request.getHeader("content-type")
                .map(s -> s.startsWith(MULTIPART_FORM_DATA.getMimeType()))
                .orElse(false)) {
            throw new BadRequestException("올바르지 않은 메시지 들어옴 - Multipart/form-data 를 받는 곳에 해당 타입이 들어오지 않음");
        }
        Optional<MimeType> optMimeType = request.getMimeType();
        if (optMimeType.isEmpty()) {
            throw new BadRequestException("올바르지 않은 메시지 들어옴 - Multipart/form-data를 받는 곳에 컨텐츠 타입이 정의되지 않음");
        }
        MimeType mimeType = optMimeType.get();
        String attributeVariable = mimeType.getAttributeVariable(BOUNDARY)
                .orElseThrow(() -> new BadRequestException("Boundary 가 정의되지 않음"));
        byte[] startBoundary = ("--" + attributeVariable + "\r\n").getBytes();
        byte[] boundary = ("\r\n--" + attributeVariable + "\r\n").getBytes();
        byte[] lastBoundary = ("\r\n--" + attributeVariable + "--").getBytes();
        List<RawMultipartData> rawMultiParts = new ArrayList<>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] body = request.getBody();
        log.info(new String(body));
        for (int i = startBoundary.length; i < body.length - boundary.length; i++) {
            if(compareByteArrays(body, i, lastBoundary)){
                rawMultiParts.add(new RawMultipartData(baos.toByteArray()));
                break;
            }
            if(compareByteArrays(body, i, boundary)){
                i += boundary.length - 1;
                rawMultiParts.add(new RawMultipartData(baos.toByteArray()));
                baos = new ByteArrayOutputStream();
                continue;
            }
            baos.write(body[i]);
        }

        List<MultipartData> multipartData = new ArrayList<>();
        for (RawMultipartData rawMultiPart : rawMultiParts) {
            byte[] data = rawMultiPart.data();
            byte[] headerBodyDelimiter = "\r\n\r\n".getBytes();

            byte[] headerBytes = null;
            byte[] bodyBytes;
            baos = new ByteArrayOutputStream();
            for (int i = 0; i <= data.length - headerBodyDelimiter.length; i++) {
                if (compareByteArrays(data, i, headerBodyDelimiter)){
                    headerBytes = baos.toByteArray();
                    break;
                }
                baos.write(data[i]);
                log.info(baos.toString());
            }
            if(headerBytes == null){
                throw new BadRequestException("multipart/form-data 바디에 헤더가 없음 - Content-Disposition 헤더가 존재해야 함");
            }
            baos = new ByteArrayOutputStream();
            for (int i = headerBytes.length + headerBodyDelimiter.length; i<data.length; i++){
                baos.write(data[i]);
            }
            bodyBytes = baos.toByteArray();
            multipartData.add(new MultipartData(headerBytes, bodyBytes));
        }

        return multipartData;
    }

    static boolean compareByteArrays(byte[] target, int targetIndex, byte[] reference) {
        if (targetIndex + reference.length > target.length) {
            return false;
        }
        for (int i = 0; i < reference.length; i++) {
            if (reference[i] != target[targetIndex + i]) return false;
        }
        return true;
    }
}
