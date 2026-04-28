package com.soulin.api.post.controller;

import com.soulin.api.global.jwt.CustomUserPrincipal;
import com.soulin.api.post.dto.*;
import com.soulin.api.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostSummaryResponse>> getPosts(@RequestParam(required = false) Integer colorId){
        List<PostSummaryResponse> response = postService.getPosts(colorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts")
    public ResponseEntity<PostDetailResponse> createDraftPost(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody CreatePostRequest request
    ){
        PostDetailResponse response = postService.createDraftPost(principal.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/me/posts")
    public ResponseEntity<List<MyPostSummaryResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(required = false) String tab
    ){
        List<MyPostSummaryResponse> response = postService.getMyPosts(principal.getUserId(), tab);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId
    ){
        PostDetailResponse response = postService.getPostDetail(principal.getUserId(), postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/me/posts/{postId}")
    public ResponseEntity<PostDetailResponse> getMyPostDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId
    ){
        PostDetailResponse response = postService.getMyPostDetail(principal.getUserId(), postId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request){
        PostDetailResponse response = postService.updatePost(principal.getUserId(), postId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId
    ){
        System.out.println("DELETE HIT principal=" + (principal != null ? principal.getUserId() : null) + ", postId=" + postId);
        postService.deletePost(principal.getUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/publish")
    public ResponseEntity<PublishPostResponse> publishPost(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody PublishPostRequest request
    ){
        PublishPostResponse response = postService.publishPost(principal.getUserId(), request);
        return ResponseEntity.ok(response);
    }
}
