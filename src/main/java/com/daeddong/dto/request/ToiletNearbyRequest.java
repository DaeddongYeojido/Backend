package com.daeddong.dto.request;

import com.daeddong.domain.Toilet;
import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ToiletNearbyRequest {

    @NotNull(message = "위도(lat)는 필수입니다.")
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double lat;

    @NotNull(message = "경도(lng)는 필수입니다.")
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double lng;

    @Min(100) @Max(5000)
    private double radius = 1000;

    private Toilet.OpenStatus openStatus;
    private Boolean isDisabled;

    public void init(Double lat, Double lng, double radius,
                     String openStatus, Boolean isDisabled) {
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.isDisabled = isDisabled;
        if (openStatus != null) {
            try {
                this.openStatus = Toilet.OpenStatus.valueOf(openStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new DaeddongException(ErrorCode.INVALID_OPEN_STATUS);
            }
        }
    }
}
