package com.soulin.api.color.controller;

import com.soulin.api.color.dto.ColorResponse;
import com.soulin.api.color.dto.RecommendPostColorRequest;
import com.soulin.api.color.dto.RecommendPostColorResponse;
import com.soulin.api.color.service.ColorRecommendationService;
import com.soulin.api.color.service.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;
    private final ColorRecommendationService colorRecommendationService;

    @GetMapping("/colors")
    public ResponseEntity<List<ColorResponse>> getColors() {
        return ResponseEntity.ok(colorService.getColors());
    }

    @PostMapping("/posts/color-recommendation")
    public ResponseEntity<RecommendPostColorResponse> recommendPostColor(
            @Valid @RequestBody RecommendPostColorRequest request
    ) {
        RecommendPostColorResponse response = colorRecommendationService.recommend(request);
        return ResponseEntity.ok(response);
    }
}
