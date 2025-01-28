package template;

import provider.Model;

import java.util.Map;

public class TemplateEngine {
    private static final TemplateEngine INSTANCE = new TemplateEngine();
    private TemplateEngine(){}

    public static TemplateEngine getInstance(){
        return INSTANCE;
    }

    public String renderTemplate(byte[] fileData, Model model){
        String htmlContent = new String(fileData);

        Map<String, Object> data = model.getData();

        for(Map.Entry<String, Object> entry : data.entrySet()){
            htmlContent = htmlContent.replace(wrapInHtmlComment(entry.getKey()), String.valueOf(entry.getValue()));
        }

        return htmlContent;
    }

    private String wrapInHtmlComment(String input){
        return String.format("<!-- %s -->", input);
    }
}
