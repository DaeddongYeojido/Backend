package com.daeddong.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 화장실 제보 게시판 도메인
 *
 * 지도에 없는 화장실을 새로 추가해달라고 요청하는 기능.
 * 관리자가 PENDING → APPROVED 승인하면 toilets 테이블에 실제로 등록됨.
 * REJECTED 시 반려.
 */
@Entity
@Table(name = "toilet_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToiletReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 동시 수정 제어를 위한
    @Version
    private Long version;

    /** 제보한 기기 ID */
    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    // ── 화장실 정보 (필수) ─────────────────────────────

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    // ── 화장실 정보 (선택) ─────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "open_status", length = 20)
    private Toilet.OpenStatus openStatus;

    @Column(name = "is_disabled")
    private Boolean isDisabled;

    @Column(name = "is_gender_sep")
    private Boolean isGenderSep;

    @Column(name = "open_hours", length = 100)
    private String openHours;

    // ── 제보 메타 ──────────────────────────────────────

    /** 제보자가 남기는 추가 메모 */
    @Column(length = 500)
    private String memo;

    /** 첨부 이미지 S3 URL */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** 관리자 처리 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    /** 승인 후 생성된 화장실 ID (승인 전 null) */
    @Column(name = "approved_toilet_id")
    private Long approvedToiletId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.status = ReportStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── 팩토리 ────────────────────────────────────────

    public static ToiletReport create(String deviceId,
                                      String name, String address, Double lat, Double lng,
                                      Toilet.OpenStatus openStatus,
                                      Boolean isDisabled, Boolean isGenderSep,
                                      String openHours, String memo, String imageUrl) {
        ToiletReport r = new ToiletReport();
        r.deviceId = deviceId;
        r.name = name;
        r.address = address;
        r.lat = lat;
        r.lng = lng;
        r.openStatus = openStatus;
        r.isDisabled = isDisabled;
        r.isGenderSep = isGenderSep;
        r.openHours = openHours;
        r.memo = memo;
        r.imageUrl = imageUrl;
        return r;
    }

    // ── 상태 변경 ─────────────────────────────────────

    /** 관리자 승인: 생성된 화장실 ID를 기록 */
    public void approve(Long toiletId) {
        this.status = ReportStatus.APPROVED;
        this.approvedToiletId = toiletId;
    }

    public void reject() {
        this.status = ReportStatus.REJECTED;
    }

    // ── Enum ─────────────────────────────────────────

    public enum ReportStatus {
        PENDING,   // 검토 중
        APPROVED,  // 승인 → toilets 테이블에 등록됨
        REJECTED   // 반려
    }
}
