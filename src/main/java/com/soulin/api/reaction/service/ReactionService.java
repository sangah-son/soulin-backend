package com.soulin.api.reaction.service;

import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.global.common.TimeZoneUtils;
import com.soulin.api.post.PostStatus;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.reaction.dto.CreatePostReactionRequest;
import com.soulin.api.reaction.dto.MyReactionSummaryResponse;
import com.soulin.api.reaction.dto.PostReactionDetailResponse;
import com.soulin.api.reaction.dto.PostReactionSummaryItemResponse;
import com.soulin.api.reaction.dto.PostReactionResponse;
import com.soulin.api.reaction.dto.ReactionColorStatResponse;
import com.soulin.api.reaction.dto.ReactionColorRatioResponse;
import com.soulin.api.reaction.dto.ReactionTextStatResponse;
import com.soulin.api.reaction.dto.ReactionTypeResponse;
import com.soulin.api.reaction.dto.TopReactionColorResponse;
import com.soulin.api.reaction.dto.TopReactionTypeResponse;
import com.soulin.api.reaction.entity.PostReaction;
import com.soulin.api.reaction.entity.ReactionType;
import com.soulin.api.reaction.repository.PostReactionRepository;
import com.soulin.api.reaction.repository.ReactionTypeRepository;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                TimeZoneUtils.toKst(savedPostReaction.getCreatedAt())
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
                TimeZoneUtils.toKst(postReaction.getCreatedAt())
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

    @Transactional(readOnly = true)
    public MyReactionSummaryResponse getMyReactionSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Post> posts = postRepository.findAllByUser(user);

        if (posts.isEmpty()) {
            return new MyReactionSummaryResponse(0, List.of(), List.of());
        }

        List<PostReaction> reactions = postReactionRepository.findAllByPostIn(posts);

        if (reactions.isEmpty()) {
            return new MyReactionSummaryResponse(0, List.of(), List.of());
        }

        int totalReactionCount = reactions.size();

        List<ReactionColorRatioResponse> colorRatios = buildColorRatios(reactions, totalReactionCount);
        List<PostReactionSummaryItemResponse> postReactionSummaries = buildPostReactionSummaries(reactions);

        return new MyReactionSummaryResponse(
                totalReactionCount,
                colorRatios,
                postReactionSummaries
        );
    }

    private List<ReactionColorRatioResponse> buildColorRatios(List<PostReaction> reactions, int totalReactionCount) {
        return reactions.stream()
                .collect(Collectors.groupingBy(PostReaction::getColor, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Color, Long>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().getColorId()))
                .map(entry -> new ReactionColorRatioResponse(
                        entry.getKey().getColorId(),
                        entry.getKey().getColorName(),
                        entry.getKey().getColorCode(),
                        entry.getValue().intValue(),
                        entry.getValue() * 100.0 / totalReactionCount
                ))
                .toList();
    }

    private List<PostReactionSummaryItemResponse> buildPostReactionSummaries(List<PostReaction> reactions) {
        return reactions.stream()
                .collect(Collectors.groupingBy(PostReaction::getPost))
                .entrySet().stream()
                .sorted(Map.Entry.<Post, List<PostReaction>>comparingByKey(
                        Comparator.comparing(Post::getCreatedAt).reversed()
                                .thenComparing(Post::getPostId)
                ))
                .map(entry -> new PostReactionSummaryItemResponse(
                        entry.getKey().getPostId(),
                        entry.getKey().getTitle(),
                        entry.getKey().getContent(),
                        buildTopColor(entry.getValue()),
                        buildTopReactionType(entry.getValue())
                ))
                .toList();
    }

    private TopReactionColorResponse buildTopColor(List<PostReaction> reactions) {
        Map<Color, Long> colorCounts = reactions.stream()
                .collect(Collectors.groupingBy(PostReaction::getColor, Collectors.counting()));

        Map.Entry<Color, Long> topColor = colorCounts.entrySet().stream()
                .max(Map.Entry.<Color, Long>comparingByValue()
                        .thenComparing(entry -> entry.getKey().getColorId(), Comparator.reverseOrder()))
                .orElseThrow(() -> new IllegalArgumentException("색상 공감 정보를 찾을 수 없습니다."));

        return new TopReactionColorResponse(
                topColor.getKey().getColorId(),
                topColor.getKey().getColorName(),
                topColor.getKey().getColorCode(),
                topColor.getValue().intValue()
        );
    }

    private TopReactionTypeResponse buildTopReactionType(List<PostReaction> reactions) {
        Map<ReactionType, Long> reactionTypeCounts = reactions.stream()
                .collect(Collectors.groupingBy(PostReaction::getReactionType, Collectors.counting()));

        Map.Entry<ReactionType, Long> topReactionType = reactionTypeCounts.entrySet().stream()
                .max(Map.Entry.<ReactionType, Long>comparingByValue()
                        .thenComparing(entry -> entry.getKey().getReactionTypeId(), Comparator.reverseOrder()))
                .orElseThrow(() -> new IllegalArgumentException("텍스트 공감 정보를 찾을 수 없습니다."));

        return new TopReactionTypeResponse(
                topReactionType.getKey().getReactionTypeId(),
                topReactionType.getKey().getReactionName(),
                topReactionType.getKey().getReactionText()
        );
    }

    @Transactional(readOnly = true)
    public PostReactionDetailResponse getPostReactionDetails(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        validatePublicPublishedPost(post);

        List<PostReaction> reactions = postReactionRepository.findAllByPost(post);

        return new PostReactionDetailResponse(
                post.getPostId(),
                post.getTitle(),
                buildColorStats(reactions),
                buildReactionTextStats(reactions)
        );
    }

    private List<ReactionColorStatResponse> buildColorStats(List<PostReaction> reactions) {
        int totalReactionCount = reactions.size();

        if (totalReactionCount == 0) {
            return List.of();
        }

        return reactions.stream()
                .collect(Collectors.groupingBy(PostReaction::getColor, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Color, Long>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().getColorId()))
                .map(entry -> new ReactionColorStatResponse(
                        entry.getKey().getColorId(),
                        entry.getKey().getColorName(),
                        entry.getKey().getColorCode(),
                        entry.getValue().intValue(),
                        entry.getValue() * 100.0 / totalReactionCount
                ))
                .toList();
    }

    private List<ReactionTextStatResponse> buildReactionTextStats(List<PostReaction> reactions) {
        return reactions.stream()
                .collect(Collectors.groupingBy(PostReaction::getReactionType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<ReactionType, Long>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().getReactionTypeId()))
                .map(entry -> new ReactionTextStatResponse(
                        entry.getKey().getReactionTypeId(),
                        entry.getKey().getReactionName(),
                        entry.getKey().getReactionText(),
                        entry.getValue().intValue()
                ))
                .toList();
    }
}
