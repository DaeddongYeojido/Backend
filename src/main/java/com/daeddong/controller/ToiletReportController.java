package com.daeddong.controller;

import com.daeddong.domain.ToiletReport.ReportStatus;
import com.daeddong.dto.request.ToiletReportRequest;
import com.daeddong.dto.response.ToiletReportResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.ToiletReportService;
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
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Validated
public class ToiletReportController {

    private final ToiletReportService reportService;

    /**
     * 화장실 제보 등록
     * POST /api/v1/reports
     * Content-Type: multipart/form-data
     * - data: ToiletReportRequest JSON (필수/선택 필드 포함)
     * - image: 현장 사진 (optional)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ToiletReportResponse>> createReport(
            @RequestPart("data") @Valid ToiletReportRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("제보가 등록되었습니다. 관리자 검토 후 지도에 반영됩니다.",
                        reportService.createReport(request, image)));
    }

    /**
     * 전체 제보 목록 (게시판용)
     * GET /api/v1/reports?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ToiletReportResponse>>> getReports(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReports(pageable)));
    }

    /**
     * 내 제보 목록
     * GET /api/v1/reports/my?deviceId=xxx
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<ToiletReportResponse>>> getMyReports(
            @RequestParam @NotBlank String deviceId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getMyReports(deviceId, pageable)));
    }

    /**
     * 제보 단건 조회
     * GET /api/v1/reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ToiletReportResponse>> getReport(
            @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReport(reportId)));
    }

    /**
     * 제보 삭제 (본인 + PENDING 상태만)
     * DELETE /api/v1/reports/{reportId}?deviceId=xxx
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @PathVariable Long reportId,
            @RequestParam @NotBlank String deviceId
    ) {
        reportService.deleteReport(reportId, deviceId);
        return ResponseEntity.ok(ApiResponse.ok("제보가 삭제되었습니다."));
    }

    // ── 관리자 ────────────────────────────────────────────

    /**
     * 상태별 제보 목록 (관리자)
     * GET /api/v1/reports/admin?status=PENDING
     */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<ToiletReportResponse>>> getReportsByStatus(
            @RequestParam(defaultValue = "PENDING") ReportStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReportsByStatus(status, pageable)));
    }

    /**
     * 제보 승인 → 화장실 자동 등록 (관리자)
     * PATCH /api/v1/reports/{reportId}/approve
     */
    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<ApiResponse<ToiletReportResponse>> approveReport(
            @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "제보가 승인되어 화장실이 지도에 등록되었습니다.",
                reportService.approveReport(reportId)));
    }

    /**
     * 제보 반려 (관리자)
     * PATCH /api/v1/reports/{reportId}/reject
     */
    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<ApiResponse<ToiletReportResponse>> rejectReport(
            @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("제보가 반려되었습니다.",
                reportService.rejectReport(reportId)));
    }
}
