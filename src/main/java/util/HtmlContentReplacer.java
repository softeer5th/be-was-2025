package util;

import model.User;
import webserver.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class HtmlContentReplacer {
    private static final String startIfString = "<my_if";
    private static final String endIfString = "</my_if>";
    private static final String isDynamicHtml = "<dynamic />";
    private final Boolean loggedIn;
    private final Map<String, String> userProperties = new HashMap<>();

    public HtmlContentReplacer(String sid){
        if(loggedIn = (sid != null)) {
            User user = (User) SessionManager.getSession(sid).getUser();
            userProperties.put("$userId", user.getUserId());
            userProperties.put("$userName", user.getName());
            userProperties.put("$userEmail", user.getEmail());
        }
    }

    public byte[] replace(byte[] body) {
        String html = new String(body);

        if(!html.contains(isDynamicHtml)) return body;

        html = html.replace(isDynamicHtml, "");

        int startIndex = 0;

        while ((startIndex = html.indexOf(startIfString, startIndex)) != -1) {
            int closeTagIndex = html.indexOf(">", startIndex) + 1;

            int endIndex = html.indexOf(endIfString, closeTagIndex);
            if (endIndex == -1) {
                break;
            }

            String content = html.substring(startIndex, endIndex + endIfString.length());

            String condition = html.substring(startIndex + startIfString.length(), closeTagIndex - 1).trim();

            String innerContent = html.substring(closeTagIndex, endIndex).trim();

            if (loggedIn == Boolean.parseBoolean(condition)) {
                html = html.replace(content, innerContent);
            } else {
                html = html.replace(content, "");
            }
        }

        for(String property : userProperties.keySet()){
            html = html.replace(property, userProperties.get(property));
        }

        return html.getBytes();
    }
}
