package util;

import model.Comment;
import model.Post;
import model.User;
import webserver.session.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlContentReplacer {
    private static final String startIfString = "<my_if";
    private static final String endIfString = "</my_if>";
    private static final String isDynamicHtml = "<dynamic />";
    private final String startCommentTag = "<comment_for_post>";
    private final Map<String, String> properties = new HashMap<>();
    private boolean hasPost = false;
    private int postId = -1;

    public HtmlContentReplacer(String sid){
        if(sid != null) {
            User user = (User) SessionManager.getSession(sid).getUser();
            properties.put("$userId", user.getUserId());
            properties.put("$userName", user.getName());
            properties.put("$userEmail", user.getEmail());
        }
    }

    public void setPostContent(String queryString) {
        Parameter parameter = new Parameter(queryString);
        postId = Integer.parseInt(parameter.getValue("postId"));
        if(postId != -1) {
            Post post = PostManager.getPost(postId);
            hasPost = true;
            properties.put("$postTitle", post.getTitle());
            properties.put("$postContent", post.getContent());
            properties.put("$postUserId", post.getUserId());
        }
        properties.put("$nowPost", PostManager.getNowPostId(postId));
        properties.put("$nextPost", PostManager.getNextPostId(postId));
        properties.put("$prevPost", PostManager.getPrevPostId(postId));
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

        if(html.contains(startCommentTag)) {
            html = replaceComment(html, CommentManager.getCommentsByPost(postId));
        }

        for(String property : properties.keySet()) {
            html = html.replace(property, properties.get(property));
        }

        return html.getBytes();
    }

    private String replaceComment(String html, List<Comment> comments) {
        properties.put("$commentAmmount", String.valueOf(comments.size()));
        final String endCommentTag = "</comment_for_post>";

        int startIndex = html.indexOf(startCommentTag);
        int endIndex = html.indexOf(endCommentTag, startIndex);
        if (endIndex == -1) {
            return html;
        }

        String content = html.substring(startIndex, endIndex + endCommentTag.length());
        String template = html.substring(startIndex + startCommentTag.length(), endIndex).trim();

        StringBuilder newContent = new StringBuilder();
        for (Comment comment : comments) {
            String renderedComment = template
                    .replace("$commentUserId", comment.getUserId())
                    .replace("$commentContent", comment.getContent());
            newContent.append(renderedComment).append("\n");
        }

        return html.replace(content, newContent.toString());
    }

}
