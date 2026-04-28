package com.soulin.api.reaction.repository;

import com.soulin.api.post.entity.Post;
import com.soulin.api.reaction.dto.ReceivedReactionItem;
import com.soulin.api.reaction.entity.PostReaction;
import com.soulin.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction,Long> {
    boolean existsByPostAndUser(Post post, User user);
    Optional<PostReaction> findByPostAndUser(Post post, User user);
    List<PostReaction> findAllByPost(Post post);
    void deleteAllByPost(Post post);
    List<PostReaction> findAllByPostIn(List<Post> posts);
    List<PostReaction> findAllByPostInOrderByCreatedAtDesc(List<Post> posts);

    // 내 리액션 1건 (postId + userId 기반)
    Optional<PostReaction> findByPost_PostIdAndUser_Id(Long postId, Long userId);

    // 받은 리액션을 (reactionType, color)로 group by + count
    @Query("""
    select new com.soulin.api.reaction.dto.ReceivedReactionItem(
        rt.reactionTypeId, rt.reactionName, rt.reactionText,
        c.colorId, c.colorCode,
        count(pr)
    )
    from PostReaction pr
    join pr.reactionType rt
    join pr.color c
    where pr.post.postId = :postId
    group by rt.reactionTypeId, rt.reactionName, rt.reactionText, c.colorId, c.colorCode
    order by count(pr) desc
""")
    List<ReceivedReactionItem> findReceivedReactionsByPostId(@Param("postId") Long postId);

    long countByPost_PostId(Long postId);
}
