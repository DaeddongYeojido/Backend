package com.daeddong.dto.response;

import com.daeddong.domain.CrowdVote;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CrowdVoteResponse {

    private Long toiletId;
    private String deviceId;
    private CrowdVote.CrowdLevel level;
    private LocalDateTime votedAt;
    private LocalDateTime expiresAt;

    public static CrowdVoteResponse from(CrowdVote vote) {
        return CrowdVoteResponse.builder()
                .toiletId(vote.getToilet().getId())
                .deviceId(vote.getDeviceId())
                .level(vote.getLevel())
                .votedAt(vote.getVotedAt())
                .expiresAt(vote.getExpiresAt())
                .build();
    }
}
