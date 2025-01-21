package webserver;

import response.HTTPResponse;
import constant.HTTPCode;
import db.InMemoryDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HTTPRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static manager.UserManager.COOKIE;
import static util.Utils.getSessionIdInCookie;


public class StaticURIHandler implements URIHandler {
    private static final String STATIC_FILE_DIRECTORY_PATH = "src/main/resources/static";
    private static final String INDEX_HTML = "/index.html";
    private static final Logger logger = LoggerFactory.getLogger(StaticURIHandler.class);

    @Override
    public boolean supports(HTTPRequest httpRequest) {
        File file = new File(STATIC_FILE_DIRECTORY_PATH, httpRequest.getUri());
        if (file.isDirectory()){
            file = new File(STATIC_FILE_DIRECTORY_PATH, httpRequest.getUri() + INDEX_HTML);
        }
        return file.exists();
    }

    @Override
    public HTTPResponse handle(HTTPRequest httpRequest) {
        if(httpRequest.getUri().equals("/mypage")){
            String sessionId = getSessionIdInCookie(httpRequest.getHeaderByKey(COOKIE));
            if(!InMemoryDatabase.sessionExists(sessionId)){
                return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND, INDEX_HTML);
            }
        }

        File file = new File(STATIC_FILE_DIRECTORY_PATH, httpRequest.getUri());
        if (file.isDirectory()){
            file = new File(STATIC_FILE_DIRECTORY_PATH, httpRequest.getUri() + INDEX_HTML);
        }

        logger.debug("Successfully served static file for " + httpRequest.getUri());
        return HTTPResponse.createResourceResponse(httpRequest.getHttpVersion(), HTTPCode.OK,file.getName(),fileToByteArray(file));
    }

    private byte[] fileToByteArray(File file) {
        byte[] fileBytes = new byte[(int) file.length()]; // 파일 크기만큼 배열 생성

        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(fileBytes); // 파일 내용 읽기
            if (bytesRead != fileBytes.length) {
                throw new IOException("Could not completely read the file");
            }
            return fileBytes;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
