package com.soulin.api.mypage.controller;

import com.soulin.api.global.jwt.CustomUserPrincipal;
import com.soulin.api.mypage.dto.ColorStatsResponse;
import com.soulin.api.mypage.dto.MypageSummaryResponse;
import com.soulin.api.mypage.dto.RepresentativePostRequest;
import com.soulin.api.mypage.dto.RepresentativePostResponse;
import com.soulin.api.mypage.service.MypageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me")
public class MypageController {
    private final MypageService mypageService;

    @GetMapping("/mypage/summary")
    public ResponseEntity<MypageSummaryResponse> getMypageSummary(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam int year,
            @RequestParam int month
    ) {
        MypageSummaryResponse response = mypageService.getMypageSummary(principal.getUserId(), year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/color-stats")
    public ResponseEntity<ColorStatsResponse> getColorStats(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(required = false) String range,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        ColorStatsResponse response = mypageService.getColorStats(principal.getUserId(), range, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/calendar-days/{date}/representative-post")
    public ResponseEntity<RepresentativePostResponse> selectRepresentativePost(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody RepresentativePostRequest request
    ) {
        RepresentativePostResponse response = mypageService.selectRepresentativePost(
                principal.getUserId(),
                date,
                request
        );
        return ResponseEntity.ok(response);
    }
}
