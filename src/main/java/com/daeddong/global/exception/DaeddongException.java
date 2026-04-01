package com.daeddong.global.exception;

import lombok.Getter;

@Getter
public class DaeddongException extends RuntimeException {

    private final ErrorCode errorCode;

    public DaeddongException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public DaeddongException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }
}
