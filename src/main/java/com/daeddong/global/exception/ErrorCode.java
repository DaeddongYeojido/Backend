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
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 삭제할 수 있습니다.");

    private final HttpStatus status;
    private final String message;
}
