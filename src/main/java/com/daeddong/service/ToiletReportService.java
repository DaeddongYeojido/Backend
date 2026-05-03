package com.daeddong.service;

import com.daeddong.domain.Toilet;
import com.daeddong.domain.ToiletReport;
import com.daeddong.domain.ToiletReport.ReportStatus;
import com.daeddong.dto.request.ToiletReportRequest;
import com.daeddong.dto.response.ToiletReportResponse;
import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import com.daeddong.global.s3.S3Uploader;
import com.daeddong.repository.ToiletRepository;
import com.daeddong.repository.ToiletReportRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ToiletReportService {

    private static final String S3_FOLDER = "reports";

    private final ToiletReportRepository reportRepository;
    private final ToiletRepository toiletRepository;
    private final S3Uploader s3Uploader;
    private final GeometryFactory geometryFactory;

    /** 화장실 제보 등록 */
    @Transactional
    public ToiletReportResponse createReport(ToiletReportRequest request, MultipartFile image) {

        if (!isInSeoul(request.getLat(), request.getLng())) {
            throw new DaeddongException(ErrorCode.INVALID_LOCATION);
        }

        String imageUrl = (image != null && !image.isEmpty())
                ? s3Uploader.upload(image, S3_FOLDER)
                : null;


        ToiletReport report = ToiletReport.create(
                request.getDeviceId(),
                request.getName(), request.getAddress(), request.getLat(), request.getLng(),
                request.getOpenStatus(),
                request.getIsDisabled(), request.getIsGenderSep(),
                request.getOpenHours(), request.getMemo(), imageUrl
        );

        return ToiletReportResponse.from(reportRepository.save(report));
    }

    // ToiletReportService.java
    public Page<ToiletReportResponse> getReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(ToiletReportResponse::from);
    }

    /** 본인 제보 목록 */
    public Page<ToiletReportResponse> getMyReports(String deviceId, Pageable pageable) {
        return reportRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId, pageable)
                .map(ToiletReportResponse::from);
    }

    /** 제보 단건 조회 */
    public ToiletReportResponse getReport(Long reportId) {
        return ToiletReportResponse.from(findReport(reportId));
    }

    /** 상태별 전체 조회 (관리자) */
    public Page<ToiletReportResponse> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(ToiletReportResponse::from);
    }

    /** 제보 삭제 — 본인 + PENDING 상태만 가능 */
    @Transactional
    public void deleteReport(Long reportId, String deviceId) {
        ToiletReport report = findReport(reportId);

        if (!report.getDeviceId().equals(deviceId)) {
            throw new DaeddongException(ErrorCode.REPORT_FORBIDDEN);
        }
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new DaeddongException(ErrorCode.REPORT_ALREADY_PROCESSED);
        }

        s3Uploader.delete(report.getImageUrl());
        reportRepository.delete(report);
    }

    /**
     * 관리자: 제보 승인
     * → 제보 정보를 기반으로 toilets 테이블에 새 화장실을 직접 생성
     */
    @Transactional
    public ToiletReportResponse approveReport(Long reportId) {
        ToiletReport report = findReport(reportId);

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new DaeddongException(ErrorCode.REPORT_ALREADY_PROCESSED);
        }

        if (report.getApprovedToiletId() != null) {
            throw new DaeddongException(ErrorCode.REPORT_ALREADY_PROCESSED);
        }

        // lat/lng → Point 변환 (SRID 4326)
        Point location = geometryFactory.createPoint(
                new Coordinate(report.getLng(), report.getLat())
        );

        // 선택 필드 기본값 처리
        Toilet.OpenStatus openStatus = report.getOpenStatus() != null
                ? report.getOpenStatus()
                : Toilet.OpenStatus.OPEN;
        boolean isDisabled = report.getIsDisabled() != null && report.getIsDisabled();
        boolean isGenderSep = report.getIsGenderSep() == null || report.getIsGenderSep(); // 기본 true

        Toilet toilet = Toilet.create(
                report.getName(), report.getAddress(),
                report.getLat(), report.getLng(), location,
                openStatus, isDisabled, isGenderSep,
                report.getOpenHours(), Toilet.Source.USER_REPORT
        );

        Toilet saved = toiletRepository.save(toilet);
        report.approve(saved.getId());

        return ToiletReportResponse.from(report);
    }

    /** 관리자: 제보 반려 */
    @Transactional
    public ToiletReportResponse rejectReport(Long reportId) {
        ToiletReport report = findReport(reportId);

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new DaeddongException(ErrorCode.REPORT_ALREADY_PROCESSED);
        }

        report.reject();
        return ToiletReportResponse.from(report);
    }

    // ── private ───────────────────────────────────────

    private ToiletReport findReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new DaeddongException(ErrorCode.REPORT_NOT_FOUND));
    }

    private boolean isInSeoul(double lat, double lng) {
        return (lat >= 37.41 && lat <= 37.70) &&
                (lng >= 126.73 && lng <= 127.27);
    }
}
