package com.daeddong.dto.response;

import com.daeddong.domain.PaperRequest;
import lombok.Builder;
import lombok.Getter;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaperRequestResponse {

    private Long id;
    private Long toiletId;
    private String toiletName;
    private Double toiletLat;
    private Double toiletLng;
    private String deviceId;
    private PaperRequest.Gender gender;
    private PaperRequest.RequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime rescuedAt;
    private LocalDateTime rescueDisplayUntil;
    /** 남은 활성 시간(초). ACTIVE 상태일 때만 의미 있음 */
    private long remainingSeconds;

    public static PaperRequestResponse from(PaperRequest pr) {
        LocalDateTime now = LocalDateTime.now();
        long remaining = 0;
        if (pr.getStatus() == PaperRequest.RequestStatus.ACTIVE) {
            remaining = Duration.between(now, pr.getExpiresAt()).getSeconds();
            if (remaining < 0) remaining = 0;
        }
        return PaperRequestResponse.builder()
                .id(pr.getId())
                .toiletId(pr.getToilet().getId())
                .toiletName(pr.getToilet().getName())
                .toiletLat(pr.getToilet().getLat())
                .toiletLng(pr.getToilet().getLng())
                .deviceId(pr.getDeviceId())
                .gender(pr.getGender())
                .status(pr.getStatus())
                .requestedAt(pr.getRequestedAt())
                .expiresAt(pr.getExpiresAt())
                .rescuedAt(pr.getRescuedAt())
                .rescueDisplayUntil(pr.getRescueDisplayUntil())
                .remainingSeconds(remaining)
                .build();
    }
}
