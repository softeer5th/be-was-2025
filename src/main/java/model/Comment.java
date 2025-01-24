package model;

import java.time.LocalDateTime;

public record Comment(
        int id,
        int postId,
        String userId,
        String content,
        LocalDateTime createdAt
) {
}
