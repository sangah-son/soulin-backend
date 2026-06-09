package com.soulin.api.mypage.service;

import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.mypage.dto.ColorStatsResponse;
import com.soulin.api.mypage.dto.MypageSummaryResponse;
import com.soulin.api.mypage.dto.RepresentativePostRequest;
import com.soulin.api.mypage.dto.RepresentativePostResponse;
import com.soulin.api.mypage.entity.DailyRepresentativePost;
import com.soulin.api.mypage.repository.DailyRepresentativePostRepository;
import com.soulin.api.post.PostStatus;
import com.soulin.api.post.entity.Post;
import com.soulin.api.post.repository.PostRepository;
import com.soulin.api.reaction.repository.PostReactionRepository;
import com.soulin.api.user.entity.User;
import com.soulin.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final DailyRepresentativePostRepository dailyRepresentativePostRepository;
    private final ColorRepository colorRepository;

    @Transactional(readOnly = true)
    public MypageSummaryResponse getMypageSummary(Long userId, int year, int month) {
        User user = findUser(userId);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Post> monthlyPosts = postRepository.findUserPostsWithColorByStatusAndDateRangeAsc(
                user,
                PostStatus.PUBLISHED,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        Map<LocalDate, List<Post>> postsByDate = monthlyPosts.stream()
                .collect(Collectors.groupingBy(post -> post.getCreatedAt().toLocalDate()));

        Map<LocalDate, DailyRepresentativePost> representativesByDate =
                dailyRepresentativePostRepository.findAllByUserAndRepresentDateBetween(user, startDate, endDate)
                        .stream()
                        .collect(Collectors.toMap(DailyRepresentativePost::getRepresentDate, Function.identity()));

        List<MypageSummaryResponse.CalendarDayResponse> calendarDays = postsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> buildCalendarDay(entry.getKey(), entry.getValue(), representativesByDate.get(entry.getKey())))
                .toList();

        List<MypageSummaryResponse.ColorStatResponse> colorStats = buildColorStats(monthlyPosts);
        MypageSummaryResponse.CalendarColorResponse monthlyTopColor = colorStats.stream()
                .findFirst()
                .map(stat -> new MypageSummaryResponse.CalendarColorResponse(
                        stat.getColorId(),
                        stat.getColorName(),
                        stat.getColorCode(),
                        stat.getCount(),
                        stat.getPercentage()
                ))
                .orElse(null);

        return new MypageSummaryResponse(
                user.getUserName(),
                monthlyPosts.size(),
                (int) postReactionRepository.countByPost_User_Id(userId),
                monthlyTopColor,
                calendarDays,
                colorStats
        );
    }

    @Transactional(readOnly = true)
    public ColorStatsResponse getColorStats(Long userId, String range, LocalDate startDate, LocalDate endDate) {
        User user = findUser(userId);
        LocalDate[] effectiveRange = resolveRange(range, startDate, endDate);
        LocalDate effectiveStart = effectiveRange[0];
        LocalDate effectiveEnd = effectiveRange[1];

        List<Post> posts = postRepository.findUserPostsWithColorByStatusAndDateRangeAsc(
                user,
                PostStatus.PUBLISHED,
                effectiveStart.atStartOfDay(),
                effectiveEnd.plusDays(1).atStartOfDay()
        );

        int totalCount = posts.size();
        Map<Integer, Long> colorCountMap = posts.stream()
                .collect(Collectors.groupingBy(post -> post.getColor().getColorId(), Collectors.counting()));

        List<ColorStatsResponse.ColorStatItem> colorStats = colorRepository.findAllByOrderByColorIdAsc().stream()
                .map(color -> {
                    long count = colorCountMap.getOrDefault(color.getColorId(), 0L);
                    double percentage = totalCount == 0 ? 0.0 : count * 100.0 / totalCount;
                    return new ColorStatsResponse.ColorStatItem(
                            color.getColorId(),
                            color.getColorName(),
                            color.getColorCode(),
                            (int) count,
                            percentage
                    );
                })
                .toList();

        return new ColorStatsResponse(effectiveStart, effectiveEnd, totalCount, colorStats);
    }

    private LocalDate[] resolveRange(String range, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
            }
            return new LocalDate[]{startDate, endDate};
        }
        if (startDate != null || endDate != null) {
            throw new IllegalArgumentException("startDate와 endDate는 함께 지정해야 합니다.");
        }

        LocalDate today = LocalDate.now();
        String effectiveRange = (range == null || range.isBlank()) ? "1m" : range;
        LocalDate computedStart = switch (effectiveRange) {
            case "1m" -> today.minusMonths(1);
            case "3m" -> today.minusMonths(3);
            case "6m" -> today.minusMonths(6);
            default -> throw new IllegalArgumentException("허용되지 않은 range 값입니다. (1m, 3m, 6m)");
        };
        return new LocalDate[]{computedStart, today};
    }

    public RepresentativePostResponse selectRepresentativePost(
            Long userId,
            LocalDate date,
            RepresentativePostRequest request
    ) {
        User user = findUser(userId);
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        validateRepresentativePost(userId, date, post);

        DailyRepresentativePost representativePost = dailyRepresentativePostRepository
                .findByUserAndRepresentDate(user, date)
                .map(existing -> {
                    existing.updatePost(post);
                    return existing;
                })
                .orElseGet(() -> new DailyRepresentativePost(user, post, date));

        DailyRepresentativePost savedRepresentativePost = dailyRepresentativePostRepository.save(representativePost);
        return buildRepresentativePostResponse(savedRepresentativePost);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private void validateRepresentativePost(Long userId, LocalDate date, Post post) {
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 게시글만 대표 글로 선택할 수 있습니다.");
        }

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new IllegalArgumentException("게시 완료된 글만 대표 글로 선택할 수 있습니다.");
        }

        if (!post.getCreatedAt().toLocalDate().equals(date)) {
            throw new IllegalArgumentException("해당 날짜에 작성한 게시글만 대표 글로 선택할 수 있습니다.");
        }
    }

    private MypageSummaryResponse.CalendarDayResponse buildCalendarDay(
            LocalDate date,
            List<Post> posts,
            DailyRepresentativePost savedRepresentative
    ) {
        List<Post> sortedPosts = posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed()
                        .thenComparing(Post::getPostId, Comparator.reverseOrder()))
                .toList();

        Post representative = savedRepresentative != null && posts.stream()
                .anyMatch(post -> post.getPostId().equals(savedRepresentative.getPost().getPostId()))
                ? savedRepresentative.getPost()
                : sortedPosts.get(0);

        Color representativeColor = representative.getColor();

        List<MypageSummaryResponse.SelectablePostColorResponse> selectableColors = sortedPosts.stream()
                .map(post -> new MypageSummaryResponse.SelectablePostColorResponse(
                        post.getPostId(),
                        post.getTitle(),
                        post.getColor().getColorId(),
                        post.getColor().getColorName(),
                        post.getColor().getColorCode()
                ))
                .toList();

        return new MypageSummaryResponse.CalendarDayResponse(
                date,
                posts.size(),
                representative.getPostId(),
                representativeColor.getColorId(),
                representativeColor.getColorName(),
                representativeColor.getColorCode(),
                selectableColors
        );
    }

    private List<MypageSummaryResponse.ColorStatResponse> buildColorStats(List<Post> posts) {
        int totalPostCount = posts.size();

        if (totalPostCount == 0) {
            return List.of();
        }

        return posts.stream()
                .collect(Collectors.groupingBy(Post::getColor, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Color, Long>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().getColorId()))
                .map(entry -> new MypageSummaryResponse.ColorStatResponse(
                        entry.getKey().getColorId(),
                        entry.getKey().getColorName(),
                        entry.getKey().getColorCode(),
                        entry.getValue().intValue(),
                        entry.getValue() * 100.0 / totalPostCount
                ))
                .toList();
    }

    private RepresentativePostResponse buildRepresentativePostResponse(DailyRepresentativePost representativePost) {
        Post post = representativePost.getPost();
        Color color = post.getColor();

        return new RepresentativePostResponse(
                representativePost.getRepresentDate(),
                post.getPostId(),
                color.getColorId(),
                color.getColorName(),
                color.getColorCode()
        );
    }
}
