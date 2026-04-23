package com.soulin.api.bookmark.repository;

import com.soulin.api.bookmark.entity.Bookmark;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    boolean existsByUserAndPost(User user, Post post);
    Optional<Bookmark> findByUserAndPost(User user, Post post);
    List<Bookmark> findAllByUserOrderByCreatedAtDesc(User user);
    void deleteAllByPost(Post post);
}
