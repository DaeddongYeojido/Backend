package com.daeddong.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Column(nullable = false)
    private int rating; // 1 ~ 5

    @Column(length = 500)
    private String content;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTag> tags = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static Review create(Toilet toilet, String deviceId,
                                int rating, String content, String imageUrl) {
        Review r = new Review();
        r.toilet = toilet;
        r.deviceId = deviceId;
        r.rating = rating;
        r.content = content;
        r.imageUrl = imageUrl;
        return r;
    }

    /** 태그 추가 (ReviewService에서 호출) */
    public void addTag(ReviewTag tag) {
        this.tags.add(tag);
    }
}
