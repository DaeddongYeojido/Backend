package com.daeddong.dto.response;

import com.daeddong.domain.ReviewTag;
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

    /** 혼잡도 투표 집계 (만료되지 않은 투표만) */
    private Map<String, Long> crowdSummary;

    /** 현재 대표 혼잡도 (동률 시 CROWDED > NORMAL > EMPTY 우선순위) */
    private String currentCrowd;

    /** 리뷰 평균 별점 (리뷰 없으면 null) */
    private Double averageRating;

    /** 리뷰 총 개수 */
    private long reviewCount;

    /**
     * 태그별 선택 횟수 집계
     * e.g. [{"code":"CLEAN","label":"청결해요","count":12}, ...]
     * 내림차순 정렬
     */
    private List<TagCount> tagSummary;

    // ── 동률 우선순위 ─────────────────────────────────
    private static final List<String> CROWD_PRIORITY =
            List.of("CROWDED", "NORMAL", "EMPTY");

    public static ToiletDetailResponse from(
            Toilet toilet,
            Map<String, Long> crowdSummary,
            Double averageRating,
            long reviewCount,
            List<TagCount> tagSummary) {

        String currentCrowd = null;
        if (!crowdSummary.isEmpty()) {
            long maxCount = crowdSummary.values().stream()
                    .max(Comparator.naturalOrder()).orElse(0L);
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
                .tagSummary(tagSummary)
                .build();
    }

    @Getter
    public static class TagCount {
        private final String code;   // e.g. "CLEAN"
        private final String label;  // e.g. "청결해요"
        private final long count;

        public TagCount(String code, String label, long count) {
            this.code = code;
            this.label = label;
            this.count = count;
        }

        public static TagCount of(ReviewTag.Tag tag, long count) {
            return new TagCount(tag.name(), tag.getLabel(), count);
        }
    }
}
