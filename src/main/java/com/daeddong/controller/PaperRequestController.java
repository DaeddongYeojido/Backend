package com.daeddong.controller;

import com.daeddong.dto.request.PaperRequestCreateRequest;
import com.daeddong.dto.response.ActivePaperRequestResponse;
import com.daeddong.dto.response.PaperRequestResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.PaperRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "휴지 요청", description = "긴급 휴지 요청 API 🧻")
@RestController
@RequestMapping("/api/v1/paper-requests")
@RequiredArgsConstructor
@Validated
public class PaperRequestController {

    private final PaperRequestService paperRequestService;

    @Operation(summary = "휴지 요청 생성",
            description = "500m 이내 화장실에서만 가능. 하루 1회 제한.")
    @PostMapping
    public ResponseEntity<ApiResponse<PaperRequestResponse>> createRequest(
            @Valid @RequestBody PaperRequestCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("휴지 요청이 등록되었습니다.", paperRequestService.createRequest(request)));
    }

    @Operation(summary = "구조 완료 (살았습니다! 버튼)",
            description = "요청자 본인만 가능. 완료 후 3분간 *구조* 마커 표시.")
    @PostMapping("/{requestId}/rescue")
    public ResponseEntity<ApiResponse<PaperRequestResponse>> rescue(
            @Parameter(description = "요청 ID") @PathVariable Long requestId,
            @Parameter(description = "기기 ID") @RequestParam @NotBlank String deviceId) {
        return ResponseEntity.ok(
                ApiResponse.ok("구조 완료! 정말 다행입니다 😊", paperRequestService.rescue(requestId, deviceId)));
    }

    @Operation(summary = "지도 마커용 활성 요청 목록",
            description = "PAPER_FLYING(휴지 분수) + RESCUED(*구조*) 마커. 10초 폴링 권장.")
    @GetMapping("/active-markers")
    public ResponseEntity<ApiResponse<List<ActivePaperRequestResponse>>> getActiveMarkers() {
        return ResponseEntity.ok(ApiResponse.ok(paperRequestService.getActiveMarkers()));
    }

    @Operation(summary = "특정 화장실 활성 요청 조회", description = "마커 클릭 시 상세 정보.")
    @GetMapping("/toilet/{toiletId}")
    public ResponseEntity<ApiResponse<PaperRequestResponse>> getActiveByToilet(
            @Parameter(description = "화장실 ID") @PathVariable Long toiletId) {
        return ResponseEntity.ok(ApiResponse.ok(paperRequestService.getActiveByToilet(toiletId)));
    }

    @Operation(summary = "내 요청 상태 조회",
            description = "요청자가 남은 시간(remainingSeconds) 및 상태 확인. 7초 폴링 권장.")
    @GetMapping("/{requestId}/status")
    public ResponseEntity<ApiResponse<PaperRequestResponse>> getStatus(
            @Parameter(description = "요청 ID") @PathVariable Long requestId,
            @Parameter(description = "기기 ID") @RequestParam @NotBlank String deviceId) {
        return ResponseEntity.ok(ApiResponse.ok(paperRequestService.getRequestStatus(requestId, deviceId)));
    }

    @Operation(summary = "FCM 토큰 등록/갱신",
            description = "앱 시작 시 또는 위치 변경 시 호출. 주변 알림 수신에 필요.")
    @PostMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(
            @Parameter(description = "기기 ID") @RequestParam @NotBlank String deviceId,
            @Parameter(description = "FCM 토큰") @RequestParam @NotBlank String fcmToken,
            @Parameter(description = "현재 위도") @RequestParam Double lat,
            @Parameter(description = "현재 경도") @RequestParam Double lng) {
        paperRequestService.registerFcmToken(deviceId, fcmToken, lat, lng);
        return ResponseEntity.ok(ApiResponse.ok("FCM 토큰이 등록되었습니다."));
    }
}
