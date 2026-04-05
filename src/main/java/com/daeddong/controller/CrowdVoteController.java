package com.daeddong.controller;

import com.daeddong.dto.request.CrowdVoteRequest;
import com.daeddong.dto.response.CrowdVoteResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.CrowdVoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "혼잡도", description = "화장실 혼잡도 투표 API")
@RestController
@RequestMapping("/api/v1/toilets/{toiletId}/crowd")
@RequiredArgsConstructor
public class CrowdVoteController {

    private final CrowdVoteService crowdVoteService;

    @Operation(summary = "혼잡도 투표",
            description = """
                    화장실 혼잡도를 투표합니다. (CROWDED / NORMAL / EMPTY)
                    
                    동일 기기가 동일 화장실에 이미 투표한 경우 기존 투표를 갱신(upsert)합니다.
                    투표는 설정된 시간(기본 10분) 후 만료됩니다.
                    """)
    @PostMapping
    public ResponseEntity<ApiResponse<CrowdVoteResponse>> vote(
            @Parameter(description = "화장실 ID") @PathVariable Long toiletId,
            @RequestBody @Valid CrowdVoteRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("혼잡도가 반영되었습니다.", crowdVoteService.vote(toiletId, request)));
    }
}
