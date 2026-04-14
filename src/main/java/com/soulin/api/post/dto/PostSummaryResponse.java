package com.soulin.api.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSummaryResponse {
    private Long postId;
    private String title;
    private String content;
    private Integer colorId;
    private String userName;
    private LocalDateTime createdAt;
}
