package com.soulin.api.post.repository;

import com.soulin.api.post.PostStatus;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByUser(User user);
    List<Post> findAllByStatusAndIsPublicOrderByCreatedAtDesc(PostStatus status, Boolean isPublic);
    List<Post> findAllByStatusAndIsPublicAndColor_ColorIdOrderByCreatedAtDesc(PostStatus status, Boolean isPublic, Integer colorId);
}
