package com.daeddong.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    TOILET_NOT_FOUND(HttpStatus.NOT_FOUND, "화장실 정보를 찾을 수 없습니다."),
    INVALID_CROWD_LEVEL(HttpStatus.BAD_REQUEST, "유효하지 않은 혼잡도 값입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 해당 화장실에 리뷰를 작성하셨습니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 삭제할 수 있습니다."),

    // S3 / 이미지
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "이미지 파일이 비어있습니다."),
    IMAGE_TYPE_INVALID(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (jpeg, png, webp, heic 허용)"),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지 크기는 10MB 이하여야 합니다."),

    // 제보
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "제보를 찾을 수 없습니다."),
    REPORT_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 작성한 제보만 삭제할 수 있습니다."),
    REPORT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 제보입니다."),

    // 휴지 요청
    PAPER_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "휴지 요청을 찾을 수 없습니다."),
    PAPER_REQUEST_TOO_FAR(HttpStatus.BAD_REQUEST, "화장실로부터 500m 이내에서만 휴지 요청이 가능합니다."),
    PAPER_REQUEST_DAILY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "휴지 요청은 하루에 한 번만 사용할 수 있습니다. 정말 긴급한 상황인가요?!?"),
    PAPER_REQUEST_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "이미 종료되었거나 만료된 휴지 요청입니다."),
    PAPER_REQUEST_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 요청한 건만 처리할 수 있습니다.");

    private final HttpStatus status;
    private final String message;
}
