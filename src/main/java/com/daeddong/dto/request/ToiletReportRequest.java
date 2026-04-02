package com.daeddong.dto.request;

import com.daeddong.domain.Toilet.OpenStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 화장실 제보 요청 DTO
 * Content-Type: multipart/form-data
 * - data: 이 DTO (application/json)
 * - image: 첨부 사진 (optional)
 */
@Getter
public class ToiletReportRequest {

    @NotBlank(message = "deviceId는 필수입니다.")
    private String deviceId;

    // ── 필수 ──────────────────────────────────────────

    @NotBlank(message = "화장실 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotNull(message = "위도(lat)는 필수입니다.")
    private Double lat;

    @NotNull(message = "경도(lng)는 필수입니다.")
    private Double lng;

    // ── 선택 ──────────────────────────────────────────

    /** 운영 상태 (기본값: OPEN) */
    private OpenStatus openStatus;

    /** 장애인 화장실 여부 */
    private Boolean isDisabled;

    /** 남녀 구분 여부 */
    private Boolean isGenderSep;

    /** 운영 시간 (예: "06:00 ~ 22:00", "24시간") */
    private String openHours;

    /** 제보자 추가 메모 */
    @Size(max = 500, message = "메모는 500자 이하여야 합니다.")
    private String memo;
}
