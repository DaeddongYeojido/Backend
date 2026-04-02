package com.daeddong.dto.response;

import com.daeddong.domain.Toilet.OpenStatus;
import com.daeddong.domain.ToiletReport;
import com.daeddong.domain.ToiletReport.ReportStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ToiletReportResponse {

    private Long id;
    private String deviceId;

    // 화장실 정보
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private OpenStatus openStatus;
    private Boolean isDisabled;
    private Boolean isGenderSep;
    private String openHours;

    // 제보 메타
    private String memo;
    private String imageUrl;
    private ReportStatus status;
    private Long approvedToiletId;  // 승인 후 생성된 화장실 ID
    private LocalDateTime createdAt;

    public static ToiletReportResponse from(ToiletReport report) {
        return ToiletReportResponse.builder()
                .id(report.getId())
                .deviceId(report.getDeviceId())
                .name(report.getName())
                .address(report.getAddress())
                .lat(report.getLat())
                .lng(report.getLng())
                .openStatus(report.getOpenStatus())
                .isDisabled(report.getIsDisabled())
                .isGenderSep(report.getIsGenderSep())
                .openHours(report.getOpenHours())
                .memo(report.getMemo())
                .imageUrl(report.getImageUrl())
                .status(report.getStatus())
                .approvedToiletId(report.getApprovedToiletId())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
