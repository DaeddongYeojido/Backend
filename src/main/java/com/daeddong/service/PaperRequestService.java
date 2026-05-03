package com.daeddong.service;

import com.daeddong.domain.FcmToken;
import com.daeddong.domain.PaperRequest;
import com.daeddong.domain.Toilet;
import com.daeddong.dto.request.PaperRequestCreateRequest;
import com.daeddong.dto.response.ActivePaperRequestResponse;
import com.daeddong.dto.response.PaperRequestResponse;
import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import com.daeddong.repository.FcmTokenRepository;
import com.daeddong.repository.PaperRequestRepository;
import com.daeddong.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaperRequestService {

    private static final double MAX_DISTANCE_METERS = 500.0;
    private static final double NOTIFY_RADIUS_METERS = 1000.0;

    private final PaperRequestRepository paperRequestRepository;
    private final ToiletRepository toiletRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmService fcmService;

    // ── 휴지 요청 생성 ──────────────────────────────

    @Transactional
    public PaperRequestResponse createRequest(PaperRequestCreateRequest req) {

        // 1. 화장실 존재 확인
        Toilet toilet = toiletRepository.findById(req.getToiletId())
                .orElseThrow(() -> new DaeddongException(ErrorCode.TOILET_NOT_FOUND));

        // 2. 500m 거리 검증
        double distance = calculateDistance(
                req.getLat(), req.getLng(), toilet.getLat(), toilet.getLng());
        if (distance > MAX_DISTANCE_METERS) {
            throw new DaeddongException(
                    ErrorCode.PAPER_REQUEST_TOO_FAR,
                    "화장실로부터 " + Math.round(distance) + "m 떨어져 있습니다. "
                            + "500m 이내에서만 휴지 요청이 가능합니다.");
        }

        // 3. 하루 1회 제한
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay   = LocalDate.now().atTime(LocalTime.MAX);
        if (paperRequestRepository.existsTodayRequest(req.getDeviceId(), startOfDay, endOfDay)) {
            throw new DaeddongException(ErrorCode.PAPER_REQUEST_DAILY_LIMIT_EXCEEDED);
        }

        // 4. 같은 화장실에 이미 ACTIVE 요청이 있으면 차단
        if (paperRequestRepository.existsActiveByToiletId(req.getToiletId(), LocalDateTime.now())) {
            throw new DaeddongException(ErrorCode.PAPER_REQUEST_DUPLICATE);
        }

        // 5. 저장
        PaperRequest paperRequest = PaperRequest.create(toilet, req.getDeviceId(), req.getGender());
        paperRequestRepository.save(paperRequest);
        log.info("[PaperRequest] 생성 - id={}, toiletId={}, deviceId={}, gender={}",
                paperRequest.getId(), toilet.getId(), req.getDeviceId(), req.getGender());

        // 6. 주변 1km FCM 알림 (실패해도 요청은 정상 처리)
        sendNearbyNotifications(req.getLat(), req.getLng(), req.getDeviceId(), toilet.getName());

        return PaperRequestResponse.from(paperRequest);
    }

    // ── 구조 완료 ("살았습니다." 버튼) ───────────────

    @Transactional
    public PaperRequestResponse rescue(Long requestId, String deviceId) {
        PaperRequest pr = paperRequestRepository.findByIdAndDeviceId(requestId, deviceId)
                .orElseThrow(() -> new DaeddongException(ErrorCode.PAPER_REQUEST_NOT_FOUND));

        if (!pr.isActive(LocalDateTime.now())) {
            throw new DaeddongException(ErrorCode.PAPER_REQUEST_NOT_ACTIVE);
        }

        pr.rescue();
        log.info("[PaperRequest] 구조 완료 - id={}, toiletId={}", requestId, pr.getToilet().getId());
        return PaperRequestResponse.from(pr);
    }

    // ── 지도 마커 조회 ───────────────────────────────

    /** 플러터에서 10초마다 폴링. PAPER_FLYING + RESCUED 마커 모두 반환 */
    public List<ActivePaperRequestResponse> getActiveMarkers() {
        LocalDateTime now = LocalDateTime.now();
        List<ActivePaperRequestResponse> result = new ArrayList<>();

        paperRequestRepository.findAllActive(now).stream()
                .map(ActivePaperRequestResponse::fromActive)
                .forEach(result::add);

        paperRequestRepository.findAllRescueDisplaying(now).stream()
                .map(ActivePaperRequestResponse::fromRescued)
                .forEach(result::add);

        return result;
    }

    /** 특정 화장실의 활성 요청 상세 (마커 클릭 시) */
    public PaperRequestResponse getActiveByToilet(Long toiletId) {
        return paperRequestRepository
                .findActiveByToiletId(toiletId, LocalDateTime.now())
                .map(PaperRequestResponse::from)
                .orElseThrow(() -> new DaeddongException(
                        ErrorCode.PAPER_REQUEST_NOT_FOUND, "현재 활성 휴지 요청이 없습니다."));
    }

    /** 요청자 본인 상태 폴링 (7초마다) */
    public PaperRequestResponse getRequestStatus(Long requestId, String deviceId) {
        return paperRequestRepository.findByIdAndDeviceId(requestId, deviceId)
                .map(PaperRequestResponse::from)
                .orElseThrow(() -> new DaeddongException(ErrorCode.PAPER_REQUEST_NOT_FOUND));
    }

    // ── FCM 토큰 등록/갱신 ───────────────────────────

    @Transactional
    public void registerFcmToken(String deviceId, String fcmToken, Double lat, Double lng) {
        fcmTokenRepository.findByDeviceId(deviceId).ifPresentOrElse(
                existing -> existing.update(fcmToken, lat, lng),
                () -> fcmTokenRepository.save(FcmToken.create(deviceId, fcmToken, lat, lng))
        );
        log.debug("[FCM] 토큰 등록/갱신 - deviceId={}", deviceId);
    }

    // ── 스케줄러: 만료 처리 ──────────────────────────

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expireOldRequests() {
        int count = paperRequestRepository.expireOldRequests(LocalDateTime.now());
        if (count > 0) log.info("[PaperRequest] 만료 처리 {}건", count);
    }

    // ── 내부 유틸 ────────────────────────────────────

    private void sendNearbyNotifications(double lat, double lng,
                                         String requesterDeviceId, String toiletName) {
        try {
            List<String> tokens = fcmTokenRepository.findNearbyTokens(
                    lat, lng, NOTIFY_RADIUS_METERS, requesterDeviceId);
            if (!tokens.isEmpty()) {
                fcmService.sendPaperRequestAlert(tokens, toiletName);
                log.info("[PaperRequest] FCM 알림 대상 {}명", tokens.size());
            }
        } catch (Exception e) {
            log.error("[PaperRequest] FCM 알림 실패 (요청은 정상 처리)", e);
        }
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
