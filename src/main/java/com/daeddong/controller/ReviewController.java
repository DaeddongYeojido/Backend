package com.daeddong.controller;

import com.daeddong.dto.request.ReviewRequest;
import com.daeddong.dto.response.ReviewResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/toilets/{toiletId}/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 등록
     * Content-Type: multipart/form-data
     * - data: ReviewRequest JSON 파트 (application/json)
     * - image: 이미지 파일 (optional)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long toiletId,
            @RequestPart("data") @Valid ReviewRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("리뷰가 등록되었습니다.", reviewService.createReview(toiletId, request, image)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @PathVariable Long toiletId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviews(toiletId, pageable)));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long toiletId,
            @PathVariable Long reviewId,
            @RequestParam @NotBlank String deviceId
    ) {
        reviewService.deleteReview(toiletId, reviewId, deviceId);
        return ResponseEntity.ok(ApiResponse.ok("리뷰가 삭제되었습니다."));
    }
}
