package com.daeddong.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    /**
     * toilet_id 는 집계 쿼리 성능을 위해 비정규화로 보관.
     * review → toilet 조인 없이 idx_tag_toilet 인덱스만으로 집계 가능.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Tag tag;

    public static ReviewTag create(Review review, Toilet toilet, Tag tag) {
        ReviewTag rt = new ReviewTag();
        rt.review = review;
        rt.toilet = toilet;
        rt.tag = tag;
        return rt;
    }

    /** 리뷰에 선택할 수 있는 태그 목록 */
    public enum Tag {
        CLEAN("청결해요"),
        SMELLY("냄새나요"),
        NO_PAPER("휴지없어요"),
        SPACIOUS("넓어요"),
        NARROW("좁아요"),
        WELL_MAINTAINED("관리잘돼요"),
        BROKEN("고장났어요");

        private final String label;

        Tag(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
