package com.daeddong.repository;

import com.daeddong.domain.CrowdVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CrowdVoteRepository extends JpaRepository<CrowdVote, Long> {

    @Query("""
            SELECT cv FROM CrowdVote cv
            WHERE cv.toilet.id = :toiletId
              AND cv.expiresAt > :now
            """)
    List<CrowdVote> findActiveVotes(@Param("toiletId") Long toiletId,
                                    @Param("now") LocalDateTime now);

    Optional<CrowdVote> findByToiletIdAndDeviceId(Long toiletId, String deviceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CrowdVote cv WHERE cv.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
