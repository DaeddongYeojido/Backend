package com.daeddong.controller;

import com.daeddong.dto.request.ToiletNearbyRequest;
import com.daeddong.dto.response.ToiletDetailResponse;
import com.daeddong.dto.response.ToiletSummaryResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.ToiletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "화장실", description = "화장실 조회 API")
@RestController
@RequestMapping("/api/v1/toilets")
@RequiredArgsConstructor
@Validated
public class ToiletController {

    private final ToiletService toiletService;

    @Operation(summary = "반경 내 화장실 목록 조회",
            description = "현재 위치(lat, lng) 기준 반경(radius) 내 화장실 목록을 거리순으로 반환합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<ToiletSummaryResponse>>> getNearby(
            @Parameter(description = "위도 (-90.0 ~ 90.0)", example = "37.5665") @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @Parameter(description = "경도 (-180.0 ~ 180.0)", example = "126.9780") @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lng,
            @Parameter(description = "반경 (미터, 100~5000)", example = "1000") @RequestParam(defaultValue = "1000") double radius,
            @Parameter(description = "운영 상태 필터 (OPEN / NIGHT / CLOSED)") @RequestParam(required = false) String openStatus,
            @Parameter(description = "장애인 화장실만 조회") @RequestParam(required = false) Boolean isDisabled
    ) {
        ToiletNearbyRequest request = new ToiletNearbyRequest();
        request.init(lat, lng, radius, openStatus, isDisabled);
        return ResponseEntity.ok(ApiResponse.ok(toiletService.findNearby(request)));
    }

    @Operation(summary = "가장 가까운 화장실 조회",
            description = "현재 위치 기준 5km 이내에서 가장 가까운 화장실 1건을 반환합니다.")
    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<ToiletSummaryResponse>> getNearest(
            @Parameter(description = "위도", example = "37.5665") @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @Parameter(description = "경도", example = "126.9780") @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lng
    ) {
        return ResponseEntity.ok(ApiResponse.ok(toiletService.findNearest(lat, lng)));
    }

    @Operation(summary = "화장실 상세 조회",
            description = "화장실 상세 정보 + 실시간 혼잡도 집계 + 평균 별점 + 리뷰 수를 반환합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ToiletDetailResponse>> getDetail(
            @Parameter(description = "화장실 ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(toiletService.findDetail(id)));
    }
}
