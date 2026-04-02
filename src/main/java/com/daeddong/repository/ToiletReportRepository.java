package com.daeddong.repository;

import com.daeddong.domain.ToiletReport;
import com.daeddong.domain.ToiletReport.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToiletReportRepository extends JpaRepository<ToiletReport, Long> {

    /** 상태별 전체 조회 (관리자용) */
    Page<ToiletReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    /** 기기별 본인 제보 목록 */
    Page<ToiletReport> findByDeviceIdOrderByCreatedAtDesc(String deviceId, Pageable pageable);
}
