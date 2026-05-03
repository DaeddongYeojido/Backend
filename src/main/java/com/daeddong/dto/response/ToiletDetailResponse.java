package com.daeddong.dto.response;

import com.daeddong.domain.CrowdVote;
import com.daeddong.domain.Toilet;
import lombok.Builder;
import lombok.Getter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ToiletDetailResponse {

    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private Toilet.OpenStatus openStatus;
    private boolean isDisabled;
    private boolean isGenderSep;
    private String openHours;
    private Toilet.Source source;
    private Map<String, Long> crowdSummary;
    private String currentCrowd;
    private Double averageRating;   // 리뷰 평균 별점 (리뷰 없으면 null)
    private long reviewCount;       // 리뷰 총 개수

    /**
     * 동률 시 우선순위: CROWDED > NORMAL > EMPTY
     * 투표가 없으면 null 반환
     */
    private static final List<String> CROWD_PRIORITY =
            List.of("CROWDED", "NORMAL", "EMPTY");

    public static ToiletDetailResponse from(Toilet toilet, Map<String, Long> crowdSummary,
                                            Double averageRating, long reviewCount) {
        String currentCrowd = null;
        if (!crowdSummary.isEmpty()) {
            long maxCount = crowdSummary.values().stream().max(Comparator.naturalOrder()).orElse(0L);
            currentCrowd = CROWD_PRIORITY.stream()
                    .filter(level -> crowdSummary.getOrDefault(level, 0L) == maxCount)
                    .findFirst()
                    .orElse(null);
        }

        return ToiletDetailResponse.builder()
                .id(toilet.getId())
                .name(toilet.getName())
                .address(toilet.getAddress())
                .lat(toilet.getLat())
                .lng(toilet.getLng())
                .openStatus(toilet.getOpenStatus())
                .isDisabled(toilet.isDisabled())
                .isGenderSep(toilet.isGenderSep())
                .openHours(toilet.getOpenHours())
                .source(toilet.getSource())
                .crowdSummary(crowdSummary)
                .currentCrowd(currentCrowd)
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .build();
    }
}
