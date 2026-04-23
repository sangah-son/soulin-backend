package com.soulin.api.bookmark.controller;

import com.soulin.api.bookmark.dto.BookmarkPostResponse;
import com.soulin.api.bookmark.dto.BookmarkResponse;
import com.soulin.api.bookmark.service.BookmarkService;
import com.soulin.api.global.jwt.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/posts/{postId}/bookmarks")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId
    ){
        BookmarkResponse response = bookmarkService.createBookmark(principal.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/posts/{postId}/bookmarks")
    public ResponseEntity<Void> deleteBookmark(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId
    ){
        bookmarkService.deleteBookmark(principal.getUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me/bookmarks")
    public ResponseEntity<List<BookmarkPostResponse>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        List<BookmarkPostResponse> response = bookmarkService.getMyBookmarks(principal.getUserId());
        return ResponseEntity.ok().body(response);
    }
}
