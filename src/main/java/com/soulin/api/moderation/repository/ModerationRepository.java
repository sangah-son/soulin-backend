package com.soulin.api.moderation.repository;

import com.soulin.api.moderation.entity.Moderation;
import com.soulin.api.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationRepository extends JpaRepository<Moderation,Long> {
    List<Moderation> findAllByPost(Post post);
}
