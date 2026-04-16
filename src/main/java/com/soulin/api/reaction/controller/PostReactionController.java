package com.soulin.api.reaction.controller;

import com.soulin.api.reaction.dto.CreatePostReactionRequest;
import com.soulin.api.reaction.dto.PostReactionResponse;
import com.soulin.api.reaction.dto.ReactionTypeResponse;
import com.soulin.api.reaction.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostReactionController {
    private final ReactionService reactionService;

    @GetMapping("/reaction-types")
    public ResponseEntity<List<ReactionTypeResponse>> getReactionTypes(){
        List<ReactionTypeResponse> response=reactionService.getReactionTypes();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<PostReactionResponse> createPostReaction(
            @RequestParam Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody CreatePostReactionRequest request
            ){
        PostReactionResponse response=reactionService.createPostReaction(userId, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/posts/{postId}/reactions")
    public ResponseEntity<PostReactionResponse> updatePostReaction(
            @RequestParam Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody CreatePostReactionRequest request
    ){
        PostReactionResponse response=reactionService.updatePostReaction(userId, postId, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/posts/{postId}/reactions")
    public ResponseEntity<Void> deletePostReaction(
            @RequestParam Long userId,
            @PathVariable Long postId
    ){
        reactionService.deletePostReaction(userId, postId);
        return ResponseEntity.ok().build();
    }
}
