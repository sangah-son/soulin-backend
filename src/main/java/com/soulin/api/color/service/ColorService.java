package com.soulin.api.color.service;

import com.soulin.api.color.dto.ColorResponse;
import com.soulin.api.color.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ColorService {
    private final ColorRepository colorRepository;

    public List<ColorResponse> getColors() {
        return colorRepository.findAll().stream()
                .map(color -> new ColorResponse(
                        color.getColorId(),
                        color.getColorName(),
                        color.getColorCode()
                ))
                .toList();
    }
}
