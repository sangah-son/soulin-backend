package com.soulin.api.reaction.controller;

import com.soulin.api.global.jwt.CustomUserPrincipal;
import com.soulin.api.reaction.dto.CreatePostReactionRequest;
import com.soulin.api.reaction.dto.PostReactionResponse;
import com.soulin.api.reaction.dto.ReactionTypeResponse;
import com.soulin.api.reaction.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostReactionController {
    private final ReactionService reactionService;

    @GetMapping("/reaction-types")
    public ResponseEntity<List<ReactionTypeResponse>> getReactionTypes(){
        List<ReactionTypeResponse> response = reactionService.getReactionTypes();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<PostReactionResponse> createPostReaction(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody CreatePostReactionRequest request
    ){
        PostReactionResponse response = reactionService.createPostReaction(principal.getUserId(), postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/posts/{postId}/reactions")
    public ResponseEntity<PostReactionResponse> updatePostReaction(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody CreatePostReactionRequest request
    ){
        PostReactionResponse response = reactionService.updatePostReaction(principal.getUserId(), postId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}/reactions")
    public ResponseEntity<Void> deletePostReaction(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId
    ){
        reactionService.deletePostReaction(principal.getUserId(), postId);
        return ResponseEntity.ok().build();
    }
}
