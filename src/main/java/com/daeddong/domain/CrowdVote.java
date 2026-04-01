package com.daeddong.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crowd_votes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrowdVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CrowdLevel level;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public static CrowdVote create(Toilet toilet, String deviceId,
                                   CrowdLevel level, int expireMinutes) {
        CrowdVote v = new CrowdVote();
        v.toilet = toilet;
        v.deviceId = deviceId;
        v.level = level;
        v.votedAt = LocalDateTime.now();
        v.expiresAt = v.votedAt.plusMinutes(expireMinutes);
        return v;
    }

    public void refresh(CrowdLevel level, int expireMinutes) {
        this.level = level;
        this.votedAt = LocalDateTime.now();
        this.expiresAt = this.votedAt.plusMinutes(expireMinutes);
    }

    public enum CrowdLevel { CROWDED, NORMAL, EMPTY }
}
