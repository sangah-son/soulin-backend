package com.soulin.api.color.controller;

import com.soulin.api.color.dto.ColorResponse;
import com.soulin.api.color.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;

    @GetMapping("/colors")
    public ResponseEntity<List<ColorResponse>> getColors() {
        return ResponseEntity.ok(colorService.getColors());
    }
}
