package com.daeddong.repository;

import com.daeddong.domain.PaperRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaperRequestRepository extends JpaRepository<PaperRequest, Long> {

    /** 하루 1회 제한: 오늘 이미 요청했는지 확인 */
    @Query("""
            SELECT COUNT(pr) > 0 FROM PaperRequest pr
            WHERE pr.deviceId = :deviceId
              AND pr.requestedAt >= :startOfDay
              AND pr.requestedAt < :endOfDay
            """)
    boolean existsTodayRequest(
            @Param("deviceId") String deviceId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    /** 지도 마커용: 현재 ACTIVE 상태 전체 */
    @Query("""
            SELECT pr FROM PaperRequest pr
            JOIN FETCH pr.toilet t
            WHERE pr.status = 'ACTIVE'
              AND pr.expiresAt > :now
            """)
    List<PaperRequest> findAllActive(@Param("now") LocalDateTime now);

    /** 지도 마커용: RESCUED 상태 중 *구조* 표시 기간 내 */
    @Query("""
            SELECT pr FROM PaperRequest pr
            JOIN FETCH pr.toilet t
            WHERE pr.status = 'RESCUED'
              AND pr.rescueDisplayUntil > :now
            """)
    List<PaperRequest> findAllRescueDisplaying(@Param("now") LocalDateTime now);

    /** 특정 화장실의 현재 활성 요청 (최신 1개) */
    @Query("""
            SELECT pr FROM PaperRequest pr
            JOIN FETCH pr.toilet t
            WHERE pr.toilet.id = :toiletId
              AND pr.status = 'ACTIVE'
              AND pr.expiresAt > :now
            ORDER BY pr.requestedAt DESC
            """)
    Optional<PaperRequest> findActiveByToiletId(
            @Param("toiletId") Long toiletId,
            @Param("now") LocalDateTime now
    );

    /** 본인 확인용 */
    Optional<PaperRequest> findByIdAndDeviceId(Long id, String deviceId);

    /** 스케줄러: 만료된 ACTIVE → EXPIRED 일괄 처리 */
    @Modifying
    @Transactional
    @Query("""
            UPDATE PaperRequest pr
            SET pr.status = 'EXPIRED'
            WHERE pr.status = 'ACTIVE'
              AND pr.expiresAt <= :now
            """)
    int expireOldRequests(@Param("now") LocalDateTime now);
}
