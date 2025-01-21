package util;

import model.Post;
import model.User;
import webserver.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class HtmlContentReplacer {
    private static final String startIfString = "<my_if";
    private static final String endIfString = "</my_if>";
    private static final String isDynamicHtml = "<dynamic />";
    private final Map<String, String> userProperties = new HashMap<>();
    private final Map<String, String> postProperties = new HashMap<>();
    private String userId = null;
    private boolean hasPost = false;
    private int postId = -1;

    public HtmlContentReplacer(String sid){
        if(sid != null) {
            User user = (User) SessionManager.getSession(sid).getUser();
            userId = user.getUserId();
            userProperties.put("$userId", userId);
            userProperties.put("$userName", user.getName());
            userProperties.put("$userEmail", user.getEmail());
        }
    }

    public void setPostContent(String queryString) {
        Parameter parameter = new Parameter(queryString);
        postId = Integer.parseInt(parameter.getValue("postId"));
        if(postId != -1) {
            Post post = PostManager.getPost(userId, postId);
            hasPost = true;
            postProperties.put("$postTitle", post.getTitle());
            postProperties.put("$postContent", post.getContent());
        }
        postProperties.put("$nextPost", PostManager.getNextPostId(userId, postId));
        postProperties.put("$prevPost", PostManager.getPrevPostId(userId, postId));
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

            if (hasPost == Boolean.parseBoolean(condition)) {
                html = html.replace(content, innerContent);
            } else {
                html = html.replace(content, "");
            }
        }

        for(String property : userProperties.keySet()){
            html = html.replace(property, userProperties.get(property));
        }
        for (String property : postProperties.keySet()) {
            html = html.replace(property, postProperties.get(property));
        }


        return html.getBytes();
    }
}
