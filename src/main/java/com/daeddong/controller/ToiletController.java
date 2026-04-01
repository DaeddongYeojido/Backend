package com.daeddong.controller;

import com.daeddong.dto.request.ToiletNearbyRequest;
import com.daeddong.dto.response.ToiletDetailResponse;
import com.daeddong.dto.response.ToiletSummaryResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.ToiletService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/toilets")
@RequiredArgsConstructor
@Validated
public class ToiletController {

    private final ToiletService toiletService;

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<ToiletSummaryResponse>>> getNearby(
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lng,
            @RequestParam(defaultValue = "1000") double radius,
            @RequestParam(required = false) String openStatus,
            @RequestParam(required = false) Boolean isDisabled
    ) {
        ToiletNearbyRequest request = new ToiletNearbyRequest();
        request.init(lat, lng, radius, openStatus, isDisabled);
        return ResponseEntity.ok(ApiResponse.ok(toiletService.findNearby(request)));
    }

    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<ToiletSummaryResponse>> getNearest(
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lng
    ) {
        return ResponseEntity.ok(ApiResponse.ok(toiletService.findNearest(lat, lng)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ToiletDetailResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(toiletService.findDetail(id)));
    }
}
