package com.soulin.api.post.service;

import com.soulin.api.bookmark.repository.BookmarkRepository;
import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.crisis.CrisisStatus;
import com.soulin.api.crisis.dto.CrisisDetectionResult;
import com.soulin.api.moderation.ModerationStatus;
import com.soulin.api.moderation.dto.ModerationResult;
import com.soulin.api.moderation.entity.Moderation;
import com.soulin.api.moderation.repository.ModerationRepository;
import com.soulin.api.mypage.repository.DailyRepresentativePostRepository;
import com.soulin.api.notification.repository.NotificationRepository;
import com.soulin.api.post.PostStatus;
import com.soulin.api.post.PublishStatus;
import com.soulin.api.post.dto.PublishPostRequest;
import com.soulin.api.post.dto.PublishPostResponse;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.reaction.repository.PostReactionRepository;
import com.soulin.api.safety.dto.ContentSafetyResult;
import com.soulin.api.safety.service.ContentSafetyService;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    private static final long USER_ID = 1L;
    private static final long POST_ID = 10L;

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private ColorRepository colorRepository;
    @Mock private BookmarkRepository bookmarkRepository;
    @Mock private PostReactionRepository postReactionRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private ModerationRepository moderationRepository;
    @Mock private ContentSafetyService contentSafetyService;
    @Mock private DailyRepresentativePostRepository dailyRepresentativePostRepository;

    @InjectMocks
    private PostService postService;

    private Post post;
    private PublishPostRequest request;

    @BeforeEach
    void setUp() {
        User user = new User("user@example.com", "password", "사용자");
        ReflectionTestUtils.setField(user, "id", USER_ID);
        Color color = new Color(1, "파랑", "#0000FF");
        post = new Post("제목", "본문", true, PostStatus.DRAFT, user, color);
        ReflectionTestUtils.setField(post, "postId", POST_ID);

        request = new PublishPostRequest();
        ReflectionTestUtils.setField(request, "postId", POST_ID);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
    }

    @Test
    void criticalTakesPriorityOverRejectedModerationAndSavesPrivateDraft() {
        ContentSafetyResult safetyResult = safetyResult(
                ModerationStatus.REJECTED,
                "HATE",
                CrisisStatus.CRITICAL
        );
        when(contentSafetyService.evaluate("제목", "본문")).thenReturn(safetyResult);

        PublishPostResponse response = postService.publishPost(USER_ID, request);

        assertThat(response.getStatus()).isEqualTo(PublishStatus.CRITICAL);
        assertThat(response.getModerationReason()).isNull();
        assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
        assertThat(post.getIsPublic()).isFalse();

        ArgumentCaptor<Moderation> moderationCaptor = ArgumentCaptor.forClass(Moderation.class);
        verify(moderationRepository).save(moderationCaptor.capture());
        assertThat(moderationCaptor.getValue().getStatus()).isEqualTo(ModerationStatus.REJECTED);
        assertThat(moderationCaptor.getValue().getCrisisStatus()).isEqualTo(CrisisStatus.CRITICAL);
    }

    @Test
    void crisisFailureFallsBackToApprovedModerationDecision() {
        when(contentSafetyService.evaluate("제목", "본문")).thenReturn(safetyResult(
                ModerationStatus.APPROVED,
                null,
                CrisisStatus.ERROR
        ));

        PublishPostResponse response = postService.publishPost(USER_ID, request);

        assertThat(response.getStatus()).isEqualTo(PublishStatus.APPROVED);
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(post.getIsPublic()).isTrue();
    }

    @Test
    void moderationRejectsWhenNoCrisisIsDetected() {
        when(contentSafetyService.evaluate("제목", "본문")).thenReturn(safetyResult(
                ModerationStatus.REJECTED,
                "HATE",
                CrisisStatus.NONE
        ));

        PublishPostResponse response = postService.publishPost(USER_ID, request);

        assertThat(response.getStatus()).isEqualTo(PublishStatus.REJECT);
        assertThat(response.getModerationReason()).isEqualTo("HATE");
        assertThat(post.getStatus()).isEqualTo(PostStatus.REJECTED);
    }

    private ContentSafetyResult safetyResult(
            ModerationStatus moderationStatus,
            String moderationReason,
            CrisisStatus crisisStatus
    ) {
        ModerationResult moderation = new ModerationResult(
                moderationStatus,
                moderationReason,
                moderationReason == null ? List.of() : List.of(moderationReason)
        );
        CrisisDetectionResult crisis = new CrisisDetectionResult(
                crisisStatus,
                crisisStatus == CrisisStatus.CRITICAL ? "직접" : "없음",
                0.9,
                null
        );
        return new ContentSafetyResult(moderation, crisis);
    }
}
