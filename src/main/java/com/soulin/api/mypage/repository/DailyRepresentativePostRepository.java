package com.soulin.api.mypage.repository;

import com.soulin.api.mypage.entity.DailyRepresentativePost;
import com.soulin.api.post.entity.Post;
import com.soulin.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyRepresentativePostRepository extends JpaRepository<DailyRepresentativePost, Long> {
    Optional<DailyRepresentativePost> findByUserAndRepresentDate(User user, LocalDate representDate);

    @Query("select d from DailyRepresentativePost d " +
            "join fetch d.post p join fetch p.color " +
            "where d.user = :user and d.representDate between :startDate and :endDate")
    List<DailyRepresentativePost> findAllByUserAndRepresentDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    void deleteAllByPost(Post post);
}
