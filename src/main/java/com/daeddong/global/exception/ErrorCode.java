package com.daeddong.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ── 공통 ──────────────────────────────────────────────────────────────
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다."),
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
    INVALID_PARAMETER_TYPE(HttpStatus.BAD_REQUEST, "파라미터 타입이 올바르지 않습니다."),
    REQUEST_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "요청 데이터 크기가 너무 큽니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 Content-Type입니다."),

    // ── 화장실 ────────────────────────────────────────────────────────────
    TOILET_NOT_FOUND(HttpStatus.NOT_FOUND, "화장실 정보를 찾을 수 없습니다."),
    INVALID_RADIUS(HttpStatus.BAD_REQUEST, "반경은 100m ~ 5000m 사이여야 합니다."),
    INVALID_COORDINATES(HttpStatus.BAD_REQUEST, "유효하지 않은 좌표값입니다."),
    INVALID_OPEN_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 운영 상태입니다. (OPEN / NIGHT / CLOSED)"),

    // ── 혼잡도 ────────────────────────────────────────────────────────────
    INVALID_CROWD_LEVEL(HttpStatus.BAD_REQUEST, "유효하지 않은 혼잡도 값입니다. (CROWDED / NORMAL / EMPTY)"),

    // ── 리뷰 ──────────────────────────────────────────────────────────────
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 화장실에 리뷰를 작성하셨습니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 삭제할 수 있습니다."),
    REVIEW_TOILET_MISMATCH(HttpStatus.BAD_REQUEST, "해당 화장실의 리뷰가 아닙니다."),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "별점은 1점에서 5점 사이여야 합니다."),
    REVIEW_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "리뷰 내용은 500자 이하여야 합니다."),

    // ── S3 / 이미지 ───────────────────────────────────────────────────────
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다."),
    IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "이미지 파일이 비어있습니다."),
    IMAGE_TYPE_INVALID(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (jpeg, png, webp, heic 허용)"),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지 크기는 10MB 이하여야 합니다."),

    // ── 제보 ──────────────────────────────────────────────────────────────
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "제보를 찾을 수 없습니다."),
    REPORT_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 작성한 제보만 삭제할 수 있습니다."),
    REPORT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 제보입니다."),
    REPORT_DUPLICATE(HttpStatus.CONFLICT, "동일한 위치에 이미 제보가 접수되어 있습니다."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "서울 지역만 제보 가능합니다."),

    // ── 휴지 요청 ─────────────────────────────────────────────────────────
    PAPER_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "휴지 요청을 찾을 수 없습니다."),
    PAPER_REQUEST_TOO_FAR(HttpStatus.BAD_REQUEST, "화장실로부터 500m 이내에서만 휴지 요청이 가능합니다."),
    PAPER_REQUEST_DAILY_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "휴지 요청은 하루에 한 번만 사용할 수 있습니다. 정말 긴급한 상황인가요?!?"),
    PAPER_REQUEST_NOT_ACTIVE(HttpStatus.CONFLICT, "이미 종료되었거나 만료된 휴지 요청입니다."),
    PAPER_REQUEST_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 요청한 건만 처리할 수 있습니다."),
    PAPER_REQUEST_ALREADY_RESCUED(HttpStatus.CONFLICT, "이미 구조 완료된 요청입니다."),

    // ── FCM ───────────────────────────────────────────────────────────────
    FCM_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 FCM 토큰입니다."),
    FCM_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "푸시 알림 발송에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
