package util;

import db.manage.CommentManager;
import db.manage.PostManager;
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
    private final Map<String, Boolean> conditions = new HashMap<>();
    private int postId = PostManager.getFirstPostId();

    public HtmlContentReplacer(String sid, String queryString){
        boolean login = (sid != null);
        conditions.put("login", login);
        conditions.put("hideComment", false);
        conditions.put("showAll", false);
        conditions.put("hasPost", false);
        if(login) {
            User user = (User) SessionManager.getSession(sid).getUser();
            properties.put("$userId", user.getUserId());
            properties.put("$userName", user.getName());
            properties.put("$userEmail", user.getEmail());
        }

        if(queryString!=null) {
            Parameter parameter = new Parameter(queryString);
            if(parameter.getValue("showAll") != null){
                conditions.put("showAll", true);
            }
            postId = Integer.parseInt(parameter.getValue("postId"));
        }
        setPostContent();
    }

    private void setPostContent() {
        boolean hasPost = (postId != -1);
        conditions.put("hasPost", hasPost);
        conditions.put("!hasPost", !hasPost);
        if(hasPost) {
            Post post = PostManager.getPost(postId);
            properties.put("$postTitle", post.getTitle());
            properties.put("$postContent", post.getContent());
            properties.put("$postUserId", post.getUserId());
        }
        properties.put("$nowPost", PostManager.getNowPostId(postId));
        properties.put("$nextPost", PostManager.getNextPostId(postId));
        properties.put("$prevPost", PostManager.getPrevPostId(postId));
        properties.put("$showAll", "&showAll=true");
    }

    public byte[] replace(byte[] body) {
        String html = new String(body);

        if(!html.contains(isDynamicHtml)) return body;

        html = html.replace(isDynamicHtml, "");

        if(html.contains(startCommentTag)) {
            html = replaceComment(html, CommentManager.getCommentsByPost(postId));
        }

        for(String property : properties.keySet()) {
            html = html.replace(property, properties.get(property));
        }

        if(html.contains(startIfString)) {
            html = replaceIfContent(html);
        }

        return html.getBytes();
    }

    private String replaceIfContent(String html) {
        int startIndex = 0;

        while ((startIndex = html.indexOf(startIfString, startIndex)) != -1) {
            int closeTagIndex = html.indexOf(">", startIndex) + 1;

            int nestedCount = 1;
            int searchIndex = closeTagIndex;

            while (nestedCount > 0) {
                int nextStartIf = html.indexOf(startIfString, searchIndex);
                int nextEndIf = html.indexOf(endIfString, searchIndex);

                if (nextEndIf == -1) {
                    break;
                }

                if (nextStartIf != -1 && nextStartIf < nextEndIf) {
                    nestedCount++;
                    searchIndex = nextStartIf + startIfString.length();
                } else {
                    nestedCount--;
                    searchIndex = nextEndIf + endIfString.length();
                }
            }

            if (nestedCount > 0) {
                break;
            }

            int endIndex = searchIndex - endIfString.length();
            String content = html.substring(startIndex, searchIndex);
            String condition = html.substring(startIndex + startIfString.length(), closeTagIndex - 1).trim();
            String innerContent = html.substring(closeTagIndex, endIndex).trim();

            if (conditions.get(condition)) {
                html = html.replace(content, innerContent);
            } else {
                html = html.replace(content, "");
            }

            startIndex = closeTagIndex;
        }

        return html;
    }


    private String replaceComment(String html, List<Comment> comments) {
        int size = comments.size();
        properties.put("$commentAmmount", String.valueOf(size - 3));
        boolean showAll = conditions.get("showAll");
        if(size > 3 && !showAll) {conditions.put("hideComment", true);}

        if(!showAll) {size = Math.min(size,3);}

        final String endCommentTag = "</comment_for_post>";

        int startIndex = html.indexOf(startCommentTag);
        int endIndex = html.indexOf(endCommentTag, startIndex);
        if (endIndex == -1) {
            return html;
        }

        String content = html.substring(startIndex, endIndex + endCommentTag.length());
        String template = html.substring(startIndex + startCommentTag.length(), endIndex).trim();

        StringBuilder newContent = new StringBuilder();
        for (int i=0;i<size;i++) {
            Comment comment = comments.get(i);
            String renderedComment = template
                    .replace("$commentUserId", comment.getUserId())
                    .replace("$commentContent", comment.getContent());
            newContent.append(renderedComment).append("\n");
        }

        return html.replace(content, newContent.toString());
    }

}
