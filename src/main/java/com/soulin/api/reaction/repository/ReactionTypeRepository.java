package com.soulin.api.reaction.repository;

import com.soulin.api.reaction.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionTypeRepository extends JpaRepository<ReactionType, Integer> {
}
