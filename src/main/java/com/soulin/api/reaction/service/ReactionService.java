package com.soulin.api.reaction.service;

import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.post.PostStatus;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.reaction.dto.CreatePostReactionRequest;
import com.soulin.api.reaction.dto.PostReactionResponse;
import com.soulin.api.reaction.dto.ReactionTypeResponse;
import com.soulin.api.reaction.entity.PostReaction;
import com.soulin.api.reaction.entity.ReactionType;
import com.soulin.api.reaction.repository.PostReactionRepository;
import com.soulin.api.reaction.repository.ReactionTypeRepository;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReactionService {
    private final PostReactionRepository postReactionRepository;
    private final ReactionTypeRepository reactionTypeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ColorRepository colorRepository;

    @Transactional(readOnly = true)
    public List<ReactionTypeResponse> getReactionTypes(){
        return reactionTypeRepository.findAll().stream()
                .map(reactionType -> new ReactionTypeResponse(
                        reactionType.getReactionTypeId(),
                        reactionType.getReactionName(),
                        reactionType.getReactionText()
                ))
                .toList();
    }

    public PostReactionResponse createPostReaction(Long userId, Long postId, CreatePostReactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        validatePublicPublishedPost(post);

        if (postReactionRepository.existsByPostAndUser(post, user)) {
            throw new IllegalArgumentException("이미 공감을 남겼습니다.");
        }

        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("색상을 찾을 수 없습니다."));

        ReactionType reactionType = reactionTypeRepository.findById(request.getReactionTypeId())
                .orElseThrow(() -> new IllegalArgumentException("리액션 종류를 찾을 수 없습니다."));

        PostReaction postReaction = new PostReaction(reactionType, user, post, color);
        PostReaction savedPostReaction = postReactionRepository.save(postReaction);

        return new PostReactionResponse(
                savedPostReaction.getPostReactionId(),
                savedPostReaction.getPost().getPostId(),
                savedPostReaction.getUser().getId(),
                savedPostReaction.getColor().getColorId(),
                savedPostReaction.getReactionType().getReactionTypeId(),
                savedPostReaction.getCreatedAt()
        );
    }

    public PostReactionResponse updatePostReaction(Long userId, Long postId, CreatePostReactionRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        validatePublicPublishedPost(post);

        PostReaction postReaction = postReactionRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new IllegalArgumentException("공감 정보가 존재하지 않습니다."));

        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("색상을 찾을 수 없습니다."));

        ReactionType reactionType = reactionTypeRepository.findById(request.getReactionTypeId())
                .orElseThrow(() -> new IllegalArgumentException("리액션 종류를 찾을 수 없습니다."));

        postReaction.updateReaction(reactionType, color);

        return new PostReactionResponse(
                postReaction.getPostReactionId(),
                postReaction.getPost().getPostId(),
                postReaction.getUser().getId(),
                postReaction.getColor().getColorId(),
                postReaction.getReactionType().getReactionTypeId(),
                postReaction.getCreatedAt()
        );
    }

    public void deletePostReaction(Long userId, Long postId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        validatePublicPublishedPost(post);

        PostReaction postReaction = postReactionRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new IllegalArgumentException("공감 정보가 존재하지 않습니다."));

        postReactionRepository.delete(postReaction);
    }

    private void validatePublicPublishedPost(Post post) {
        if (post.getStatus() != PostStatus.PUBLISHED || !post.getIsPublic()) {
            throw new IllegalArgumentException("공개된 게시글에만 공감을 남길 수 있습니다.");
        }
    }
}
