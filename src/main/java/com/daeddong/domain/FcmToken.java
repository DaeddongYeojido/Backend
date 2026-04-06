package com.daeddong.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_tokens",
        uniqueConstraints = @UniqueConstraint(columnNames = "device_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 100, unique = true)
    private String deviceId;

    @Column(name = "fcm_token", nullable = false, length = 500)
    private String fcmToken;

    @Column(name = "last_lat")
    private Double lastLat;

    @Column(name = "last_lng")
    private Double lastLng;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static FcmToken create(String deviceId, String fcmToken, Double lat, Double lng) {
        FcmToken t = new FcmToken();
        t.deviceId = deviceId;
        t.fcmToken = fcmToken;
        t.lastLat = lat;
        t.lastLng = lng;
        return t;
    }

    public void update(String fcmToken, Double lat, Double lng) {
        this.fcmToken = fcmToken;
        this.lastLat = lat;
        this.lastLng = lng;
    }
}
