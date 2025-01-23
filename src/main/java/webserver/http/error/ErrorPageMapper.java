package webserver.http.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import webserver.http.HttpStatus;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ErrorPageMapper {
    private static final ErrorPageMapper INSTANCE = new ErrorPageMapper();
    private static final String XML_FILE_PATH = "config/servlet.xml";
    private final Logger logger = LoggerFactory.getLogger(ErrorPageMapper.class);

    private final Map<Integer, ErrorPageInfo> errorPageMap = new HashMap<>();
    private final Map<Class<? extends Throwable>, ErrorPageInfo> exceptionPageMap = new HashMap<>();

    private ErrorPageMapper() {
        loadErrorPages();
    }

    public static ErrorPageMapper getInstance() { return INSTANCE; }

    public ErrorPageInfo getErrorPage(int status) { return errorPageMap.get(status); }

    public ErrorPageInfo getErrorPage(Class<? extends Throwable> thrownClass) { return exceptionPageMap.get(thrownClass); }

    private void loadErrorPages() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(XML_FILE_PATH).getFile());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList servletNodes = doc.getElementsByTagName("error-page");

            for (int i = 0; i < servletNodes.getLength(); i++) {
                Element errorPageElement = (Element) servletNodes.item(i);
                String location = getTagValue(errorPageElement, "location");

                String errorCode = getTagValue(errorPageElement, "error-code");
                if (errorCode != null) {
                    HttpStatus status = HttpStatus.of(Integer.parseInt(errorCode));
                    errorPageMap.put(status.getCode(), new ErrorPageInfo(location));
                    continue;
                }

                String exceptionType = getTagValue(errorPageElement, "exception-type");
                if (exceptionType != null) {
                    try {
                        Class<? extends Throwable> exceptionClass = (Class<? extends Throwable>) Class.forName(exceptionType);
                        exceptionPageMap.put(exceptionClass, new ErrorPageInfo(location));
                    } catch (ClassNotFoundException e) {
                        System.err.println("ErrorPageMapper: Exception class not found - " + exceptionType);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error loading error page mapping", e);
        }
    }

    private String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent().trim() : null;
    }

}


