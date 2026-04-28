package com.soulin.api.post.dto;

import com.soulin.api.post.PostStatus;
import com.soulin.api.reaction.dto.MyPostReactionResponse;
import com.soulin.api.reaction.dto.ReceivedReactionItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse {
    private Long postId;
    private String title;
    private String content;
    private Boolean isPublic;
    private Integer colorId;
    private Long userId;
    private String userName;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MyPostReactionResponse myReaction; //내 리액션, null 가능
    private List<ReceivedReactionItem> receivedReactions; //받은 리액션들, 비어있을 수 있음
    private Integer totalReactionCount; //총 공감 수
}
