package com.daeddong.dto.response;

import com.daeddong.domain.Toilet;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToiletSearchResponse {

    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private Toilet.OpenStatus openStatus;
    private boolean isDisabled;
    private boolean isGenderSep;

    /**
     * 현재 위치로부터의 거리 (미터).
     * lat/lng 없이 호출한 경우 null.
     */
    private Double distanceMeters;

    public static ToiletSearchResponse from(Toilet toilet, Double distanceMeters) {
        return ToiletSearchResponse.builder()
                .id(toilet.getId())
                .name(toilet.getName())
                .address(toilet.getAddress())
                .lat(toilet.getLat())
                .lng(toilet.getLng())
                .openStatus(toilet.getOpenStatus())
                .isDisabled(toilet.isDisabled())
                .isGenderSep(toilet.isGenderSep())
                .distanceMeters(distanceMeters)
                .build();
    }
}
