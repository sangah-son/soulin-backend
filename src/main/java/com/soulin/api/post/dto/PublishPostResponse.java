package com.soulin.api.post.dto;

import com.soulin.api.post.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublishPostResponse {
    private Long postId;
    private PostStatus status;
    private String message;
    private String moderationReason;
}
