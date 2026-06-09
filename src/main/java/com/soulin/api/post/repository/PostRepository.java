package com.soulin.api.post.repository;

import com.soulin.api.post.PostStatus;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByUser(User user);
    List<Post> findAllByUserOrderByCreatedAtDesc(User user);
    List<Post> findAllByUserAndStatusOrderByCreatedAtDesc(User user, PostStatus status);
    List<Post> findAllByUserAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(
            User user,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
    List<Post> findAllByUserAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
            User user,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
    @Query("select p from Post p join fetch p.color join fetch p.user " +
            "where p.user = :user and p.status = :status " +
            "and p.createdAt >= :start and p.createdAt < :end " +
            "order by p.createdAt asc")
    List<Post> findUserPostsWithColorByStatusAndDateRangeAsc(
            @Param("user") User user,
            @Param("status") PostStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("select p from Post p join fetch p.color join fetch p.user " +
            "where p.user = :user and p.status = :status " +
            "and p.createdAt >= :start and p.createdAt < :end " +
            "order by p.createdAt desc")
    List<Post> findUserPostsWithColorByStatusAndDateRangeDesc(
            @Param("user") User user,
            @Param("status") PostStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("select p from Post p where p.user = :user and (p.status = :status or p.isPublic = :isPublic) order by p.createdAt desc")
    List<Post> findMyDraftOrPrivatePosts(@Param("user") User user, @Param("status") PostStatus status, @Param("isPublic") Boolean isPublic);

    @Query("select p from Post p where p.status = :status and p.isPublic = :isPublic order by coalesce(p.publishedAt, p.createdAt) desc")
    List<Post> findPublicPostsOrderByPublishedAtDesc(@Param("status") PostStatus status, @Param("isPublic") Boolean isPublic);

    List<Post> findAllByUserAndStatusAndIsPublic(User user, PostStatus status, Boolean isPublic);

    @Query("select p from Post p where p.status = :status and p.isPublic = :isPublic and p.color.colorId = :colorId order by coalesce(p.publishedAt, p.createdAt) desc")
    List<Post> findPublicPostsByColorOrderByPublishedAtDesc(@Param("status") PostStatus status, @Param("isPublic") Boolean isPublic, @Param("colorId") Integer colorId);
}
