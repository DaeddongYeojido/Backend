package com.daeddong.dto.request;

import com.daeddong.domain.CrowdVote;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CrowdVoteRequest {

    @NotBlank(message = "기기 ID는 필수입니다.")
    @Size(max = 100)
    private String deviceId;

    @NotNull(message = "혼잡도 수준은 필수입니다.")
    private CrowdVote.CrowdLevel level;
}
