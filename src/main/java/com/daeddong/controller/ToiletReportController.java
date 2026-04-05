package com.daeddong.controller;

import com.daeddong.domain.ToiletReport.ReportStatus;
import com.daeddong.dto.request.ToiletReportRequest;
import com.daeddong.dto.response.ToiletReportResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.ToiletReportService;
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

@Tag(name = "제보", description = "화장실 제보 API")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Validated
public class ToiletReportController {

    private final ToiletReportService reportService;

    @Operation(summary = "제보 등록",
            description = "새 화장실 위치를 익명으로 제보합니다. multipart/form-data: data(JSON) + image(파일, 선택)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ToiletReportResponse>> createReport(
            @RequestPart("data") @Valid ToiletReportRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("제보가 등록되었습니다. 관리자 검토 후 지도에 반영됩니다.",
                        reportService.createReport(request, image)));
    }

    @Operation(summary = "전체 제보 목록 조회", description = "게시판 형태로 전체 제보 목록을 페이지네이션으로 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ToiletReportResponse>>> getReports(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReports(pageable)));
    }

    @Operation(summary = "내 제보 목록 조회", description = "deviceId 기준 내가 제보한 목록을 반환합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<ToiletReportResponse>>> getMyReports(
            @Parameter(description = "기기 고유 ID") @RequestParam @NotBlank String deviceId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getMyReports(deviceId, pageable)));
    }

    @Operation(summary = "제보 단건 조회")
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ToiletReportResponse>> getReport(
            @Parameter(description = "제보 ID") @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReport(reportId)));
    }

    @Operation(summary = "제보 삭제", description = "본인(deviceId 일치) + PENDING 상태인 제보만 삭제 가능합니다.")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @Parameter(description = "제보 ID") @PathVariable Long reportId,
            @Parameter(description = "기기 고유 ID") @RequestParam @NotBlank String deviceId
    ) {
        reportService.deleteReport(reportId, deviceId);
        return ResponseEntity.ok(ApiResponse.ok("제보가 삭제되었습니다."));
    }

    // ── 관리자 ────────────────────────────────────────────

    @Operation(summary = "[관리자] 상태별 제보 목록 조회", description = "status 파라미터로 PENDING / APPROVED / REJECTED 필터링")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<ToiletReportResponse>>> getReportsByStatus(
            @Parameter(description = "처리 상태") @RequestParam(defaultValue = "PENDING") ReportStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReportsByStatus(status, pageable)));
    }

    @Operation(summary = "[관리자] 제보 승인", description = "제보를 승인하면 화장실이 지도에 자동 등록됩니다.")
    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<ApiResponse<ToiletReportResponse>> approveReport(
            @Parameter(description = "제보 ID") @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "제보가 승인되어 화장실이 지도에 등록되었습니다.",
                reportService.approveReport(reportId)));
    }

    @Operation(summary = "[관리자] 제보 반려")
    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<ApiResponse<ToiletReportResponse>> rejectReport(
            @Parameter(description = "제보 ID") @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("제보가 반려되었습니다.",
                reportService.rejectReport(reportId)));
    }
}
