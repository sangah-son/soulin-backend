package com.soulin.api.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class BookmarkResponse {
    private Long bookmarkId;
    private Long postId;
    private Long userId;
    private OffsetDateTime createdAt;
}
