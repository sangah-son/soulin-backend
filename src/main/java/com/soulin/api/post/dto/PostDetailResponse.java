package com.soulin.api.post.dto;

import com.soulin.api.post.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDetailResponse {
    private Long postId;
    private String title;
    private String content;
    private Boolean isPublic;
    private Integer colorId;
    private String userName;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
