package com.daeddong.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ReviewRequest {

    @NotBlank(message = "deviceId는 필수입니다.")
    private String deviceId;

    @NotNull(message = "rating은 필수입니다.")
    @Min(value = 1, message = "별점은 최소 1점입니다.")
    @Max(value = 5, message = "별점은 최대 5점입니다.")
    private Integer rating;

    @Size(max = 500, message = "리뷰 내용은 500자 이하여야 합니다.")
    private String content;
}
