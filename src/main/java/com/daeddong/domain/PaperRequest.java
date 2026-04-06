package com.daeddong.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "paper_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaperRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /** 요청 만료 시각 (요청 후 7분) */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "rescued_at")
    private LocalDateTime rescuedAt;

    /** 구조 완료 후 *구조* 마커 표시 종료 시각 (구조 후 3분) */
    @Column(name = "rescue_display_until")
    private LocalDateTime rescueDisplayUntil;

    private static final int EXPIRE_MINUTES = 7;
    private static final int RESCUE_DISPLAY_MINUTES = 3;

    public static PaperRequest create(Toilet toilet, String deviceId, Gender gender) {
        PaperRequest r = new PaperRequest();
        r.toilet = toilet;
        r.deviceId = deviceId;
        r.gender = gender;
        r.requestedAt = LocalDateTime.now();
        r.expiresAt = r.requestedAt.plusMinutes(EXPIRE_MINUTES);
        r.status = RequestStatus.ACTIVE;
        return r;
    }

    public void rescue() {
        this.status = RequestStatus.RESCUED;
        this.rescuedAt = LocalDateTime.now();
        this.rescueDisplayUntil = this.rescuedAt.plusMinutes(RESCUE_DISPLAY_MINUTES);
    }

    public boolean isActive(LocalDateTime now) {
        return this.status == RequestStatus.ACTIVE && now.isBefore(this.expiresAt);
    }

    public boolean isRescueDisplaying(LocalDateTime now) {
        return this.status == RequestStatus.RESCUED
                && this.rescueDisplayUntil != null
                && now.isBefore(this.rescueDisplayUntil);
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum RequestStatus {
        ACTIVE,   // 요청 중 (7분간)
        RESCUED,  // 구조 완료
        EXPIRED   // 시간 초과 종료
    }
}
