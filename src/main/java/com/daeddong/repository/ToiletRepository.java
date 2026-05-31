package com.daeddong.repository;

import com.daeddong.domain.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ToiletRepository extends JpaRepository<Toilet, Long>, ToiletRepositoryCustom {

    @Query(value = """
            SELECT *
            FROM toilets
            WHERE ST_Distance_Sphere(location, ST_SRID(POINT(:lng, :lat), 4326)) <= :radius
            ORDER BY ST_Distance_Sphere(location, ST_SRID(POINT(:lng, :lat), 4326))
            """, nativeQuery = true)
    List<Toilet> findNearby(@Param("lat") double lat,
                            @Param("lng") double lng,
                            @Param("radius") double radius);

    /**
     * 키워드 검색: 이름 또는 주소에 keyword 포함된 화장실을 반환.
     * - lat/lng 있으면 거리순, 없으면 이름순
     * - CLOSED 상태 제외
     * - 최대 limit건 반환
     */
    @Query(value = """
            SELECT *,
                   CASE
                       WHEN :lat IS NOT NULL AND :lng IS NOT NULL
                           THEN ST_Distance_Sphere(location, ST_SRID(POINT(:lng, :lat), 4326))
                       ELSE NULL
                   END AS distance
            FROM toilets
            WHERE open_status != 'CLOSED'
              AND (name    LIKE CONCAT('%', :keyword, '%')
                OR address LIKE CONCAT('%', :keyword, '%'))
            ORDER BY
                CASE
                    WHEN :lat IS NOT NULL AND :lng IS NOT NULL
                        THEN ST_Distance_Sphere(location, ST_SRID(POINT(:lng, :lat), 4326))
                    ELSE NULL
                END ASC,
                name ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Toilet> searchByKeyword(@Param("keyword") String keyword,
                                 @Param("lat") Double lat,
                                 @Param("lng") Double lng,
                                 @Param("limit") int limit);
}
