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
    INVALID_CROWD_LEVEL(HttpStatus.BAD_REQUEST, "유효하지 않은 혼잡도 값입니다.");

    private final HttpStatus status;
    private final String message;
}
