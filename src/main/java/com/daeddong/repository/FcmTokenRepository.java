package com.daeddong.repository;

import com.daeddong.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByDeviceId(String deviceId);

    /** 반경 radiusMeters 내 FCM 토큰 목록. 요청자 본인 제외. */
    @Query(value = """
            SELECT ft.fcm_token
            FROM fcm_tokens ft
            WHERE ft.device_id != :excludeDeviceId
              AND ft.last_lat IS NOT NULL
              AND ft.last_lng IS NOT NULL
              AND ST_Distance_Sphere(
                    POINT(ft.last_lng, ft.last_lat),
                    POINT(:lng, :lat)
                  ) <= :radiusMeters
            """, nativeQuery = true)
    List<String> findNearbyTokens(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusMeters") double radiusMeters,
            @Param("excludeDeviceId") String excludeDeviceId
    );
}
