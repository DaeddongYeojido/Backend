package com.daeddong.domain;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Table(name = "toilets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Toilet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Column(nullable = false, columnDefinition = "POINT")
    private Point location;

    @Enumerated(EnumType.STRING)
    @Column(name = "open_status", nullable = false, length = 20)
    private OpenStatus openStatus;

    @Column(name = "is_disabled", nullable = false)
    private boolean isDisabled;

    @Column(name = "is_gender_sep", nullable = false)
    private boolean isGenderSep;

    @Column(name = "open_hours", length = 100)
    private String openHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Source source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Toilet create(String name, String address,
                                Double lat, Double lng, Point location,
                                OpenStatus openStatus,
                                boolean isDisabled, boolean isGenderSep,
                                String openHours, Source source) {
        Toilet t = new Toilet();
        t.name = name;
        t.address = address;
        t.lat = lat;
        t.lng = lng;
        t.location = location;
        t.openStatus = openStatus;
        t.isDisabled = isDisabled;
        t.isGenderSep = isGenderSep;
        t.openHours = openHours;
        t.source = source;
        return t;
    }

    public void update(String name, String address,
                       OpenStatus openStatus,
                       boolean isDisabled, boolean isGenderSep,
                       String openHours) {
        this.name = name;
        this.address = address;
        this.openStatus = openStatus;
        this.isDisabled = isDisabled;
        this.isGenderSep = isGenderSep;
        this.openHours = openHours;
    }

    public enum OpenStatus { OPEN, NIGHT, CLOSED }
    public enum Source { PUBLIC_DATA, USER_REPORT }
}
