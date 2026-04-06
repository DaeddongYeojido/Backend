package com.daeddong.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FcmService {

    /**
     * 휴지 요청 긴급 알림 발송.
     * 주변 1km 사용자에게 전송.
     */
    public void sendPaperRequestAlert(List<String> tokens, String toiletName) {
        if (tokens == null || tokens.isEmpty()) {
            log.debug("[FCM] 주변 수신자 없음 - 발송 생략");
            return;
        }

        for (List<String> chunk : partition(tokens, 500)) {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(chunk)
                    .setNotification(Notification.builder()
                            .setTitle("🧻 긴급 휴지 요청!")
                            .setBody("일생일대의 급박한 사람을 도와주세요... 😭 (" + toiletName + ")")
                            .build())
                    .putData("type", "PAPER_REQUEST")
                    .putData("toiletName", toiletName)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setBadge(1)
                                    .build())
                            .build())
                    .build();

            try {
                BatchResponse response = FirebaseMessaging.getInstance()
                        .sendEachForMulticast(message);
                log.info("[FCM] 발송 완료 - 성공: {}, 실패: {}",
                        response.getSuccessCount(), response.getFailureCount());

                if (response.getFailureCount() > 0) {
                    List<SendResponse> responses = response.getResponses();
                    for (int i = 0; i < responses.size(); i++) {
                        if (!responses.get(i).isSuccessful()) {
                            log.warn("[FCM] 발송 실패 - token={}, reason={}",
                                    chunk.get(i),
                                    responses.get(i).getException() != null
                                            ? responses.get(i).getException().getMessage()
                                            : "unknown");
                        }
                    }
                }
            } catch (FirebaseMessagingException e) {
                log.error("[FCM] 발송 오류", e);
            }
        }
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
