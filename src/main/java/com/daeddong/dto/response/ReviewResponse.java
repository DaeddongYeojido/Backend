package com.daeddong.dto.response;

import com.daeddong.domain.Review;
import com.daeddong.domain.ReviewTag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReviewResponse {

    private Long id;
    private Long toiletId;
    private String deviceId;
    private int rating;
    private String content;
    private String imageUrl;
    private List<TagInfo> tags;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        List<TagInfo> tagInfos = review.getTags().stream()
                .map(rt -> new TagInfo(rt.getTag().name(), rt.getTag().getLabel()))
                .collect(Collectors.toList());

        return ReviewResponse.builder()
                .id(review.getId())
                .toiletId(review.getToilet().getId())
                .deviceId(review.getDeviceId())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .tags(tagInfos)
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Getter
    public static class TagInfo {
        private final String code;   // e.g. "CLEAN"
        private final String label;  // e.g. "청결해요"

        public TagInfo(String code, String label) {
            this.code = code;
            this.label = label;
        }
    }
}
