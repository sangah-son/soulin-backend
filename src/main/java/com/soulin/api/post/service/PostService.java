package com.soulin.api.post.service;

import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.bookmark.repository.BookmarkRepository;
import com.soulin.api.moderation.ModerationStatus;
import com.soulin.api.moderation.dto.ModerationResult;
import com.soulin.api.moderation.entity.Moderation;
import com.soulin.api.moderation.repository.ModerationRepository;
import com.soulin.api.moderation.service.ModerationService;
import com.soulin.api.post.PostStatus;
import com.soulin.api.post.dto.CreatePostRequest;
import com.soulin.api.post.dto.MyPostSummaryResponse;
import com.soulin.api.post.dto.PostDetailResponse;
import com.soulin.api.post.dto.PostSummaryResponse;
import com.soulin.api.post.dto.PublishPostRequest;
import com.soulin.api.post.dto.PublishPostResponse;
import com.soulin.api.post.dto.UpdatePostRequest;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.reaction.dto.MyPostReactionResponse;
import com.soulin.api.reaction.dto.ReceivedReactionItem;
import com.soulin.api.reaction.repository.PostReactionRepository;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ColorRepository colorRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostReactionRepository postReactionRepository;
    private final ModerationRepository moderationRepository;
    private final ModerationService moderationService;

    public PostDetailResponse createDraftPost(Long userId, CreatePostRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("색상을 찾을 수 없습니다."));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                request.getIsPublic(),
                PostStatus.DRAFT,
                user,
                color
        );

        Post savedPost = postRepository.save(post);

        // 새 글에는 리액션이 없으므로 myReaction, receivedReactions는 null, totalReactionCount는 0
        return buildPostDetailResponse(savedPost, userId);
    }

    @Transactional(readOnly = true)
    public List<MyPostSummaryResponse> getMyPosts(Long userId, String tab){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<Post> posts = findMyPostsByTab(user, tab);

        return posts.stream()
                .map(post -> new MyPostSummaryResponse(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getIsPublic(),
                        post.getStatus(),
                        post.getColor().getColorId(),
                        post.getUser().getUserName(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ))
                .toList();
    }

    private List<Post> findMyPostsByTab(User user, String tab) {
        if (tab == null || tab.isBlank()) {
            return postRepository.findAllByUserOrderByCreatedAtDesc(user);
        }

        return switch (tab) {
            case "published" -> postRepository.findAllByUserAndStatusOrderByCreatedAtDesc(user, PostStatus.PUBLISHED);
            case "draft-private" -> postRepository.findMyDraftOrPrivatePosts(user, PostStatus.DRAFT, false);
            case "rejected" -> postRepository.findAllByUserAndStatusOrderByCreatedAtDesc(user, PostStatus.REJECTED);
            default -> throw new IllegalArgumentException("허용되지 않은 tab 값입니다.");
        };
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long userId, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (post.getStatus() != PostStatus.PUBLISHED || !post.getIsPublic()) {
            throw new IllegalArgumentException("공개된 게시글만 조회할 수 있습니다.");
        }

        return buildPostDetailResponse(post, userId);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getMyPostDetail(Long userId, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        validatePostOwner(userId, post);

        return buildPostDetailResponse(post, userId);
    }

    public PostDetailResponse updatePost(Long userId, Long postId, UpdatePostRequest request){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        validatePostOwner(userId, post);

        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("색상을 찾을 수 없습니다."));

        post.updatePost(
                request.getTitle(),
                request.getContent(),
                request.getIsPublic(),
                color
        );

        return buildPostDetailResponse(post, userId);
    }

    public void deletePost(Long userId, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        validatePostOwner(userId, post);

        bookmarkRepository.deleteAllByPost(post);
        postReactionRepository.deleteAllByPost(post);
        moderationRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getPosts(Integer colorId){
        List<Post> posts = colorId == null
                ? postRepository.findAllByStatusAndIsPublicOrderByCreatedAtDesc(PostStatus.PUBLISHED, true)
                : postRepository.findAllByStatusAndIsPublicAndColor_ColorIdOrderByCreatedAtDesc(PostStatus.PUBLISHED, true, colorId);

        return posts.stream()
                .map(post -> new PostSummaryResponse(
                        post.getPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getColor().getColorId(),
                        post.getUser().getId(),
                        post.getUser().getUserName(),
                        post.getCreatedAt()
                ))
                .toList();
    }

    public PublishPostResponse publishPost(Long userId, PublishPostRequest request){
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        validatePostOwner(userId, post);

        ModerationResult moderationResult = moderationService.moderate(
                post.getTitle(),
                post.getContent()
        );

        Moderation moderation = new Moderation(
                moderationResult.getStatus(),
                moderationResult.getReason(),
                post
        );

        moderationRepository.save(moderation);

        if (moderationResult.getStatus() == ModerationStatus.APPROVED) {
            post.publish();
            return new PublishPostResponse(
                    post.getPostId(),
                    PostStatus.PUBLISHED,
                    "피드에 게시되었습니다.",
                    null
            );
        }

        post.reject();
        return new PublishPostResponse(
                post.getPostId(),
                PostStatus.REJECTED,
                "게시가 반려되었습니다.",
                moderationResult.getReason()
        );
    }

    private void validatePostOwner(Long userId, Post post){
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 게시글만 처리할 수 있습니다.");
        }
    }

    /**
     * PostDetailResponse 공통 빌더.
     * - myReaction: 호출자가 이 글에 남긴 리액션. 안 남겼으면 null.
     * - receivedReactions: (텍스트+색) 조합별 카운트 리스트. 0건이면 null.
     * - totalReactionCount: 이 글이 받은 리액션 총 개수.
     */
    private PostDetailResponse buildPostDetailResponse(Post post, Long userId) {
        MyPostReactionResponse myReaction = postReactionRepository
                .findByPost_PostIdAndUser_Id(post.getPostId(), userId)
                .map(pr -> new MyPostReactionResponse(
                        pr.getPostReactionId(),
                        pr.getReactionType().getReactionTypeId(),
                        pr.getReactionType().getReactionName(),
                        pr.getReactionType().getReactionText(),
                        pr.getColor().getColorId(),
                        pr.getColor().getColorName(),
                        pr.getColor().getColorCode()
                ))
                .orElse(null);

        List<ReceivedReactionItem> received =
                postReactionRepository.findReceivedReactionsByPostId(post.getPostId());
        List<ReceivedReactionItem> receivedReactions =
                (received == null || received.isEmpty()) ? null : received;

        long totalCount = postReactionRepository.countByPost_PostId(post.getPostId());

        return new PostDetailResponse(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getIsPublic(),
                post.getColor().getColorId(),
                post.getUser().getId(),
                post.getUser().getUserName(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                myReaction,
                receivedReactions,
                (int) totalCount
        );
    }
}
