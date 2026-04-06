package com.daeddong.dto.request;

import com.daeddong.domain.PaperRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaperRequestCreateRequest {

    @NotNull(message = "화장실 ID는 필수입니다.")
    private Long toiletId;

    @NotBlank(message = "기기 ID는 필수입니다.")
    @Size(max = 100)
    private String deviceId;

    @NotNull(message = "성별 선택은 필수입니다. (MALE / FEMALE)")
    private PaperRequest.Gender gender;

    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double lat;

    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double lng;
}
