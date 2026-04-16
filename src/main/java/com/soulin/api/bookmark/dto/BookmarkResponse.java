package com.soulin.api.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookmarkResponse {
    private Long bookmarkId;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
}
