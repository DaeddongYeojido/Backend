package com.daeddong.controller;

import com.daeddong.domain.ReviewTag;
import com.daeddong.dto.response.ReviewResponse.TagInfo;
import com.daeddong.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "리뷰 태그", description = "리뷰 태그 API")
@RestController
@RequestMapping("/api/v1/review-tags")
@RequiredArgsConstructor
public class ReviewTagController {

    /**
     * GET /api/v1/review-tags
     * 선택 가능한 태그 전체 목록 반환 (Flutter 앱 초기 로드용)
     * 서버에서 Enum을 관리하므로 클라이언트는 하드코딩 없이 이 API로 목록을 받아 쓴다.
     */
    @Operation(summary = "선택 가능한 태그 목록 조회",
            description = "리뷰 작성 시 선택할 수 있는 태그 전체 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TagInfo>>> getAvailableTags() {
        List<TagInfo> tags = Arrays.stream(ReviewTag.Tag.values())
                .map(tag -> new TagInfo(tag.name(), tag.getLabel()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(tags));
    }
}
