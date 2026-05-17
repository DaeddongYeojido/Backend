package com.daeddong.dto.request;

import com.daeddong.domain.ReviewTag;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 선택 태그 목록 (최대 3개, 선택 사항)
     * 가능한 값: CLEAN | SMELLY | NO_PAPER | SPACIOUS | NARROW | WELL_MAINTAINED | BROKEN
     */
    @Size(max = 3, message = "태그는 최대 3개까지 선택 가능합니다.")
    private List<ReviewTag.Tag> tags = new ArrayList<>();
}
