package com.daeddong.dto.response;

import com.daeddong.domain.Toilet;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class ToiletDetailResponse {

    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private Toilet.OpenStatus openStatus;
    private boolean isDisabled;
    private boolean isGenderSep;
    private String openHours;
    private Toilet.Source source;
    private Map<String, Long> crowdSummary;
    private String currentCrowd;

    public static ToiletDetailResponse from(Toilet toilet, Map<String, Long> crowdSummary) {
        String currentCrowd = crowdSummary.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return ToiletDetailResponse.builder()
                .id(toilet.getId())
                .name(toilet.getName())
                .address(toilet.getAddress())
                .lat(toilet.getLat())
                .lng(toilet.getLng())
                .openStatus(toilet.getOpenStatus())
                .isDisabled(toilet.isDisabled())
                .isGenderSep(toilet.isGenderSep())
                .openHours(toilet.getOpenHours())
                .source(toilet.getSource())
                .crowdSummary(crowdSummary)
                .currentCrowd(currentCrowd)
                .build();
    }
}
