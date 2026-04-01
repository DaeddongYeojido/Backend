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
}
