package com.soulin.api.post.dto;

import com.soulin.api.post.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyPostSummaryResponse {
    private Long postId;
    private String title;
    private String content;
    private Boolean isPublic;
    private PostStatus status;
    private Integer colorId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
