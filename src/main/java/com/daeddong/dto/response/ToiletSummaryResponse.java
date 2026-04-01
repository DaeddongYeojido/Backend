package com.daeddong.dto.response;

import com.daeddong.domain.Toilet;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToiletSummaryResponse {

    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private Toilet.OpenStatus openStatus;
    private boolean isDisabled;
    private boolean isGenderSep;

    public static ToiletSummaryResponse from(Toilet toilet) {
        return ToiletSummaryResponse.builder()
                .id(toilet.getId())
                .name(toilet.getName())
                .address(toilet.getAddress())
                .lat(toilet.getLat())
                .lng(toilet.getLng())
                .openStatus(toilet.getOpenStatus())
                .isDisabled(toilet.isDisabled())
                .isGenderSep(toilet.isGenderSep())
                .build();
    }
}
