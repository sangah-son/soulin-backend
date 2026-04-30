package com.soulin.api.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class BookmarkPostResponse {
    private Long bookmarkId;
    private Long postId;
    private String title;
    private String content;
    private Integer colorId;
    private String userName;
    private OffsetDateTime createdAt;
}
