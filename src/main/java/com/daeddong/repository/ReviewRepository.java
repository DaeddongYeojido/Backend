package com.daeddong.repository;

import com.daeddong.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByToiletIdOrderByCreatedAtDesc(Long toiletId, Pageable pageable);

    Optional<Review> findByToiletIdAndDeviceId(Long toiletId, String deviceId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.toilet.id = :toiletId")
    Double findAverageRatingByToiletId(@Param("toiletId") Long toiletId);

    long countByToiletId(Long toiletId);
}
