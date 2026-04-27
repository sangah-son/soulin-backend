package com.soulin.api.post.repository;

import com.soulin.api.post.PostStatus;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByUser(User user);
    List<Post> findAllByUserOrderByCreatedAtDesc(User user);
    List<Post> findAllByUserAndStatusOrderByCreatedAtDesc(User user, PostStatus status);
    @Query("select p from Post p where p.user = :user and (p.status = :status or p.isPublic = :isPublic) order by p.createdAt desc")
    List<Post> findMyDraftOrPrivatePosts(@Param("user") User user, @Param("status") PostStatus status, @Param("isPublic") Boolean isPublic);
    List<Post> findAllByStatusAndIsPublicOrderByCreatedAtDesc(PostStatus status, Boolean isPublic);
    List<Post> findAllByStatusAndIsPublicAndColor_ColorIdOrderByCreatedAtDesc(PostStatus status, Boolean isPublic, Integer colorId);
}
