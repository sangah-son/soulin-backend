package com.soulin.api.global.seed;

import com.soulin.api.color.entity.Color;
import com.soulin.api.color.repository.ColorRepository;
import com.soulin.api.reaction.entity.ReactionType;
import com.soulin.api.reaction.repository.ReactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ColorRepository colorRepository;
    private final ReactionTypeRepository reactionTypeRepository;

    @Override
    public void run(String... args) {
        seedColors();
        seedReactionTypes();
    }

    private void seedColors() {
        // PK(colorId)가 지정된 상태로 saveAll → 신규 row는 INSERT, 기존 row는 UPDATE(merge)
        // 따라서 hex가 바뀌어도 서버 재시작만으로 자동 동기화됨.
        colorRepository.saveAll(List.of(
                new Color(1, "RED", "#F21A14"),
                new Color(2, "ORANGE", "#FA6E2C"),
                new Color(3, "YELLOW", "#F8B420"),
                new Color(4, "LIGHTGREEN", "#90D12C"),
                new Color(5, "GREEN", "#219352"),
                new Color(6, "LIGHTBLUE", "#2DE1F5"),
                new Color(7, "BLUE", "#266BDE"),
                new Color(8, "NAVY", "#1B4163"),
                new Color(9, "PURPLE", "#7F32F1"),
                new Color(10, "PINK", "#F7416B"),
                new Color(11, "GREY", "#818285"),
                new Color(12, "BLACK", "#2B2B2B")
        ));
    }

    private void seedReactionTypes() {
        if (reactionTypeRepository.count() > 0) {
            return;
        }

        reactionTypeRepository.saveAll(List.of(
                new ReactionType(1, "공감", "나도 그래"),
                new ReactionType(2, "공감", "완전 공감해"),
                new ReactionType(3, "공감", "어떤 기분인지 알아"),
                new ReactionType(4, "공감", "나도 그런 적 있어"),
                new ReactionType(5, "공감", "너를 이해해"),
                new ReactionType(6, "응원", "넌 특별해"),
                new ReactionType(7, "응원", "가보자구!"),
                new ReactionType(8, "응원", "할 수 있어"),
                new ReactionType(9, "응원", "잘하고 있어"),
                new ReactionType(10, "응원", "끝까지 해보자"),
                new ReactionType(11, "위로", "괜찮아"),
                new ReactionType(12, "위로", "토닥토닥"),
                new ReactionType(13, "위로", "기다릴게"),
                new ReactionType(14, "위로", "힘들었겠다"),
                new ReactionType(15, "위로", "최선을 다했네!"),
                new ReactionType(16, "지지", "너를 믿어"),
                new ReactionType(17, "지지", "네 선택을 존중해"),
                new ReactionType(18, "지지", "리스펙해"),
                new ReactionType(19, "지지", "충분히 멋져"),
                new ReactionType(20, "지지", "있는 그대로도 좋아"),
                new ReactionType(21, "지지", "축하해")
        ));
    }
}
