package com.daeddong.repository;

import com.daeddong.domain.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {

    /**
     * 화장실 기준 태그별 개수 집계
     * 반환: [태그명, 개수] Object[] 배열 리스트
     * → ToiletDetailResponse의 tagSummary에 사용
     */
    @Query("""
            SELECT rt.tag, COUNT(rt)
            FROM ReviewTag rt
            WHERE rt.toilet.id = :toiletId
            GROUP BY rt.tag
            ORDER BY COUNT(rt) DESC
            """)
    List<Object[]> countByToiletIdGroupByTag(@Param("toiletId") Long toiletId);
}
