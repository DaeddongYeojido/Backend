package com.daeddong.controller;

import com.daeddong.dto.request.ReviewRequest;
import com.daeddong.dto.response.ReviewResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "리뷰", description = "화장실 리뷰 API")
@RestController
@RequestMapping("/api/v1/toilets/{toiletId}/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록",
            description = "화장실에 리뷰를 등록합니다. 기기당 화장실 1개 리뷰 제한. multipart/form-data: data(JSON) + image(파일, 선택)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Parameter(description = "화장실 ID") @PathVariable Long toiletId,
            @RequestPart("data") @Valid ReviewRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("리뷰가 등록되었습니다.", reviewService.createReview(toiletId, request, image)));
    }

    @Operation(summary = "리뷰 목록 조회", description = "화장실 리뷰 목록을 최신순 페이지네이션으로 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @Parameter(description = "화장실 ID") @PathVariable Long toiletId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviews(toiletId, pageable)));
    }

    @Operation(summary = "리뷰 삭제", description = "본인(deviceId 일치)이 작성한 리뷰만 삭제 가능합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @Parameter(description = "화장실 ID") @PathVariable Long toiletId,
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @Parameter(description = "기기 고유 ID") @RequestParam @NotBlank String deviceId
    ) {
        reviewService.deleteReview(toiletId, reviewId, deviceId);
        return ResponseEntity.ok(ApiResponse.ok("리뷰가 삭제되었습니다."));
    }
}
