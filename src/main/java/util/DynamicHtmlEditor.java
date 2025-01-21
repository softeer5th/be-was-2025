package util;

import model.Comment;

import java.util.List;

public class DynamicHtmlEditor {
    private static final String DYNAMIC_PREFIX = "dynamic";

    public static String edit(String content, String field, String value) {
        String target = String.format("{{ %s:%s }}", DYNAMIC_PREFIX, field);
        StringBuilder sb = new StringBuilder(content);

        int index = sb.indexOf(target);
        if (index == -1) {
            return content;
        }

        sb.replace(index, index + target.length(), value);
        return sb.toString();
    }

    public static String commentElement(List<Comment> comments) {
        StringBuilder commentBuilder = new StringBuilder();

        for (Comment comment : comments) {
            String element = getCommentElement();
            element = edit(element, "comment_user", comment.getUser().getName());
            element = edit(element, "comment_content", comment.getContent());
            commentBuilder.append(element);
        }
        return commentBuilder.toString();
    }

    private static String getCommentElement() {
        return """
        <li class="comment__item">
            <div class="comment__item__user">
              <img class="comment__item__user__img" />
              <p class="comment__item__user__nickname">{{ dynamic:comment_user }}</p>
            </div>
            <p class="comment__item__article">
              {{ dynamic:comment_content }}
            </p>
        </li>
        """;
    }

    private static String getHiddenCommentElement() {
        return """
                  <li class="comment__item hidden">
                    <div class="comment__item__user">
                      <img class="comment__item__user__img" />
                      <p class="comment__item__user__nickname">{{ dynamic:comment_user }}</p>
                    </div>
                    <p class="comment__item__article">{{ dynamic:comment_content }}</p>
                  </li>
                """;
    }
}
