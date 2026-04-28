package com.soulin.api.color.repository;

import com.soulin.api.color.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color,Integer> {
    Optional<Color> findByColorName(String colorName);
}
