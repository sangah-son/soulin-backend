package com.soulin.api.post.controller;

import com.soulin.api.post.dto.*;
import com.soulin.api.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<PostDetailResponse> createDraftPost(
            @RequestParam Long userId,
            @Valid @RequestBody CreatePostRequest request
            ){
        PostDetailResponse response=postService.createDraftPost(userId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/users/me/posts")
    public ResponseEntity<List<MyPostSummaryResponse>> getMyPosts(@RequestParam Long userId){
        List<MyPostSummaryResponse> response=postService.getMyPosts(userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId){
        PostDetailResponse response=postService.getPostDetail(postId);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request){
        PostDetailResponse response=postService.updatePost(postId, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId){
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/publish")
    public ResponseEntity<PublishPostResponse> publishPost(
            @Valid @RequestBody PublishPostRequest request
    ){
        PublishPostResponse response=postService.publishPost(request);
        return ResponseEntity.ok(response);
    }
}
