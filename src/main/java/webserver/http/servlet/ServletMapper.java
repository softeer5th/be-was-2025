package webserver.http.servlet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServletMapper {
    private final Map<String, ServletInfo> servletMap = new HashMap<>();
    private static final String XML_FILE_PATH = "config/servlet.xml";

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
                String url = servletElement.getElementsByTagName("url").item(0).getTextContent();
                String method = servletElement.getElementsByTagName("method").item(0).getTextContent();
                String className = servletElement.getElementsByTagName("class").item(0).getTextContent();

                servletMap.put(url + ":" + method, new ServletInfo(url, method, className));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServletInfo getServlet(String url, String method) {
        return servletMap.get(url + ":" + method);
    }
}