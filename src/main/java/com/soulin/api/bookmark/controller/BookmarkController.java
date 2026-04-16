package com.soulin.api.bookmark.controller;

import com.soulin.api.bookmark.dto.BookmarkPostResponse;
import com.soulin.api.bookmark.dto.BookmarkResponse;
import com.soulin.api.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/posts/{postId}/bookmarks")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @RequestParam Long userId,
            @PathVariable Long postId
    ){
        BookmarkResponse response=bookmarkService.createBookmark(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/posts/{postId}/bookmarks")
    public ResponseEntity<Void> deleteBookmark(
            @RequestParam Long userId,
            @PathVariable Long postId
    ){
        bookmarkService.deleteBookmark(userId, postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me/bookmarks")
    public ResponseEntity<List<BookmarkPostResponse>> getMyBookmarks(@RequestParam Long userId){
        List<BookmarkPostResponse> response=bookmarkService.getMyBookmarks(userId);
        return ResponseEntity.ok().body(response);
    }
}
