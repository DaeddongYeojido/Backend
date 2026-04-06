package com.daeddong.dto.response;

import com.daeddong.domain.PaperRequest;
import lombok.Builder;
import lombok.Getter;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 지도 마커 표시용 응답 DTO.
 * 플러터 클라이언트가 /active-markers 를 10초마다 폴링.
 */
@Getter
@Builder
public class ActivePaperRequestResponse {

    private Long requestId;
    private Long toiletId;
    private Double toiletLat;
    private Double toiletLng;

    /**
     * PAPER_FLYING : 휴지 분수 애니메이션 (ACTIVE 7분간)
     * RESCUED      : *구조* 텍스트 (구조 완료 후 3분간)
     */
    private MarkerDisplayType displayType;

    private PaperRequest.Gender gender;
    private LocalDateTime expiresAt;
    private LocalDateTime rescueDisplayUntil;
    private long remainingSeconds;

    public static ActivePaperRequestResponse fromActive(PaperRequest pr) {
        LocalDateTime now = LocalDateTime.now();
        long remaining = Duration.between(now, pr.getExpiresAt()).getSeconds();
        return ActivePaperRequestResponse.builder()
                .requestId(pr.getId())
                .toiletId(pr.getToilet().getId())
                .toiletLat(pr.getToilet().getLat())
                .toiletLng(pr.getToilet().getLng())
                .displayType(MarkerDisplayType.PAPER_FLYING)
                .gender(pr.getGender())
                .expiresAt(pr.getExpiresAt())
                .rescueDisplayUntil(null)
                .remainingSeconds(Math.max(remaining, 0))
                .build();
    }

    public static ActivePaperRequestResponse fromRescued(PaperRequest pr) {
        LocalDateTime now = LocalDateTime.now();
        long remaining = Duration.between(now, pr.getRescueDisplayUntil()).getSeconds();
        return ActivePaperRequestResponse.builder()
                .requestId(pr.getId())
                .toiletId(pr.getToilet().getId())
                .toiletLat(pr.getToilet().getLat())
                .toiletLng(pr.getToilet().getLng())
                .displayType(MarkerDisplayType.RESCUED)
                .gender(pr.getGender())
                .expiresAt(pr.getExpiresAt())
                .rescueDisplayUntil(pr.getRescueDisplayUntil())
                .remainingSeconds(Math.max(remaining, 0))
                .build();
    }

    public enum MarkerDisplayType {
        PAPER_FLYING,
        RESCUED
    }
}
