package com.soulin.api.reaction.repository;

import com.soulin.api.post.entity.Post;
import com.soulin.api.reaction.entity.PostReaction;
import com.soulin.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction,Long> {
    boolean existsByPostAndUser(Post post, User user);
    Optional<PostReaction> findByPostAndUser(Post post, User user);
    void deleteAllByPost(Post post);
}
