package webserver.http.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import webserver.http.HttpStatus;
import webserver.http.servlet.exception.DuplicateServletMappingException;
import webserver.http.servlet.exception.ServletException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServletMapper {
    private final Map<String, ServletInfo> servletMap = new HashMap<>();
    private static final String XML_FILE_PATH = "config/servlet.xml";
    private static final Logger logger = LoggerFactory.getLogger(ServletMapper.class);

    public ServletMapper() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(XML_FILE_PATH).getFile());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList servletNodes = doc.getElementsByTagName("servlet");

            for (int i = 0; i < servletNodes.getLength(); i++) {
                Element servletElement = (Element) servletNodes.item(i);
                String url = Optional.ofNullable(servletElement.getElementsByTagName("url").item(0))
                        .map(Node::getTextContent)
                        .orElseThrow(() -> new IOException("Missing <url> tag in servlet mapping"));

                String method = Optional.ofNullable(servletElement.getElementsByTagName("method").item(0))
                        .map(Node::getTextContent)
                        .orElseThrow(() -> new IOException("Missing <method> tag in servlet mapping"));

                String className = Optional.ofNullable(servletElement.getElementsByTagName("class").item(0))
                        .map(Node::getTextContent)
                        .orElseThrow(() -> new IOException("Missing <class> tag in servlet mapping"));

                String endPoint = url + ":" + method;

                if(servletMap.containsKey(endPoint)) throw new DuplicateServletMappingException(
                        String.format("Duplicate mapping found for URL '%s' and method '%s'. " +
                                        "Mapped classes: '%s' and '%s'.",
                                url, method, servletMap.get(endPoint).className(), className
                ));

                servletMap.put(endPoint, new ServletInfo(url, method, className));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ServletInfo getServlet(String url, String method) throws ServletException {
        if(!isMappedByUrl(url)) {
            throw new ServletException(HttpStatus.NOT_FOUND, String.format("Not found url '%s'", url));
        }

        ServletInfo servletInfo = servletMap.get(url + ":" + method);
        if(servletInfo == null) {
            throw new ServletException(HttpStatus.METHOD_NOT_ALLOWED, String.format("Method '%s' is not supported in '%s", method, url));
        }

        return servletInfo;
    }

    public boolean isMappedByUrl(String url) {
        return servletMap.keySet().stream().anyMatch( key -> key.startsWith(url + ":"));
    }
}