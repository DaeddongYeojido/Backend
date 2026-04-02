package com.daeddong.dto.response;

import com.daeddong.domain.Review;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {

    private Long id;
    private Long toiletId;
    private String deviceId;
    private int rating;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .toiletId(review.getToilet().getId())
                .deviceId(review.getDeviceId())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
