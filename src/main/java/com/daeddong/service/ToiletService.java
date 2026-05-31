package com.daeddong.service;

import com.daeddong.domain.ReviewTag;
import com.daeddong.domain.Toilet;
import com.daeddong.dto.request.ToiletNearbyRequest;
import com.daeddong.dto.response.ToiletDetailResponse;
import com.daeddong.dto.response.ToiletDetailResponse.TagCount;
import com.daeddong.dto.response.ToiletSearchResponse;
import com.daeddong.dto.response.ToiletSummaryResponse;
import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import com.daeddong.repository.CrowdVoteRepository;
import com.daeddong.repository.ReviewRepository;
import com.daeddong.repository.ReviewTagRepository;
import com.daeddong.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ToiletService {

    private final ToiletRepository toiletRepository;
    private final CrowdVoteRepository crowdVoteRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewTagRepository reviewTagRepository;

    public List<ToiletSummaryResponse> findNearby(ToiletNearbyRequest request) {
        boolean hasFilter = request.getOpenStatus() != null || request.getIsDisabled() != null;

        List<Toilet> toilets = hasFilter
                ? toiletRepository.findNearbyWithFilter(
                request.getLat(), request.getLng(), request.getRadius(),
                request.getOpenStatus(), request.getIsDisabled())
                : toiletRepository.findNearby(
                request.getLat(), request.getLng(), request.getRadius());

        return toilets.stream()
                .map(ToiletSummaryResponse::from)
                .collect(Collectors.toList());
    }

    public ToiletDetailResponse findDetail(Long toiletId) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new DaeddongException(ErrorCode.TOILET_NOT_FOUND));

        // 혼잡도 집계 (만료되지 않은 투표만)
        Map<String, Long> crowdSummary = crowdVoteRepository
                .findActiveVotes(toiletId, LocalDateTime.now())
                .stream()
                .collect(Collectors.groupingBy(
                        v -> v.getLevel().name(), Collectors.counting()));

        // 리뷰 통계
        Double averageRating = reviewRepository.findAverageRatingByToiletId(toiletId);
        long reviewCount = reviewRepository.countByToiletId(toiletId);

        // 태그 집계: [tag, count] Object[] → TagCount DTO 변환
        List<TagCount> tagSummary = reviewTagRepository
                .countByToiletIdGroupByTag(toiletId)
                .stream()
                .map(row -> TagCount.of((ReviewTag.Tag) row[0], (Long) row[1]))
                .collect(Collectors.toList());

        return ToiletDetailResponse.from(
                toilet, crowdSummary, averageRating, reviewCount, tagSummary);
    }

    public ToiletSummaryResponse findNearest(double lat, double lng) {
        return toiletRepository.findNearby(lat, lng, 5000)
                .stream()
                .findFirst()
                .map(ToiletSummaryResponse::from)
                .orElseThrow(() -> new DaeddongException(
                        ErrorCode.TOILET_NOT_FOUND, "주변 5km 내 화장실이 없습니다."));
    }

    // ── 키워드 검색 ──────────────────────────────────────────────────────

    private static final int SEARCH_MAX_RESULTS = 20;

    /**
     * 이름 또는 주소에 keyword가 포함된 화장실 검색.
     * - 2자 이상, 50자 이하
     * - CLOSED 제외
     * - lat/lng 있으면 거리순 + distanceMeters 포함, 없으면 이름순
     * - 최대 20건
     */
    public List<ToiletSearchResponse> search(String keyword, Double lat, Double lng) {
        String trimmed = keyword.trim();
        if (trimmed.length() < 2) {
            throw new DaeddongException(ErrorCode.SEARCH_KEYWORD_TOO_SHORT);
        }
        if (trimmed.length() > 50) {
            throw new DaeddongException(ErrorCode.SEARCH_KEYWORD_TOO_LONG);
        }

        return toiletRepository
                .searchByKeyword(trimmed, lat, lng, SEARCH_MAX_RESULTS)
                .stream()
                .map(toilet -> {
                    Double distance = (lat != null && lng != null)
                            ? calculateDistance(lat, lng, toilet.getLat(), toilet.getLng())
                            : null;
                    return ToiletSearchResponse.from(toilet, distance);
                })
                .collect(Collectors.toList());
    }

    /** Haversine 공식으로 두 좌표 간 거리(미터) 계산 */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
