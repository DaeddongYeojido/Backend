package com.daeddong.service;

import com.daeddong.domain.Toilet;
import com.daeddong.dto.request.ToiletNearbyRequest;
import com.daeddong.dto.response.ToiletDetailResponse;
import com.daeddong.dto.response.ToiletSummaryResponse;
import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import com.daeddong.repository.CrowdVoteRepository;
import com.daeddong.repository.ReviewRepository;
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

        Map<String, Long> crowdSummary = crowdVoteRepository
                .findActiveVotes(toiletId, LocalDateTime.now())
                .stream()
                .collect(Collectors.groupingBy(v -> v.getLevel().name(), Collectors.counting()));

        Double averageRating = reviewRepository.findAverageRatingByToiletId(toiletId);
        long reviewCount = reviewRepository.countByToiletId(toiletId);

        return ToiletDetailResponse.from(toilet, crowdSummary, averageRating, reviewCount);
    }

    public ToiletSummaryResponse findNearest(double lat, double lng) {
        return toiletRepository.findNearby(lat, lng, 5000)
                .stream()
                .findFirst()
                .map(ToiletSummaryResponse::from)
                .orElseThrow(() -> new DaeddongException(
                        ErrorCode.TOILET_NOT_FOUND, "주변 5km 내 화장실이 없습니다."));
    }
}
