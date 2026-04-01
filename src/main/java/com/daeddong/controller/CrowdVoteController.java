package com.daeddong.controller;

import com.daeddong.dto.request.CrowdVoteRequest;
import com.daeddong.dto.response.CrowdVoteResponse;
import com.daeddong.global.response.ApiResponse;
import com.daeddong.service.CrowdVoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/toilets/{toiletId}/crowd")
@RequiredArgsConstructor
public class CrowdVoteController {

    private final CrowdVoteService crowdVoteService;

    @PostMapping
    public ResponseEntity<ApiResponse<CrowdVoteResponse>> vote(
            @PathVariable Long toiletId,
            @RequestBody @Valid CrowdVoteRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("혼잡도가 반영되었습니다.", crowdVoteService.vote(toiletId, request)));
    }
}
