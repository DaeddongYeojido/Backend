package com.daeddong.global.exception;

import com.daeddong.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 비즈니스 예외 ──────────────────────────────────────────────────────

    @ExceptionHandler(DaeddongException.class)
    public ResponseEntity<ApiResponse<Void>> handleDaeddong(DaeddongException e) {
        log.warn("[DaeddongException] code={}, message={}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ApiResponse.fail(e.getMessage()));
    }

    // ── @Valid 검증 실패 ───────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("[ValidationException] {}", message);
        return ResponseEntity.badRequest().body(ApiResponse.fail(message));
    }

    // ── 필수 쿼리 파라미터 누락 ────────────────────────────────────────────

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException e) {
        String message = "필수 파라미터 '" + e.getParameterName() + "'가 누락되었습니다.";
        log.warn("[MissingParam] {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(message));
    }

    // ── 파라미터 타입 불일치 (예: Long 자리에 문자열) ───────────────────────

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = "파라미터 '" + e.getName() + "'의 값이 올바르지 않습니다.";
        log.warn("[TypeMismatch] {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(message));
    }

    // ── JSON 파싱 실패 / Enum 값 오류 ──────────────────────────────────────

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("[NotReadable] {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(ErrorCode.INVALID_ENUM_VALUE.getMessage()));
    }

    // ── 파일 크기 초과 ─────────────────────────────────────────────────────

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("[MaxUploadSize] {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.IMAGE_SIZE_EXCEEDED.getStatus())
                .body(ApiResponse.fail(ErrorCode.IMAGE_SIZE_EXCEEDED.getMessage()));
    }

    // ── HTTP 메서드 불일치 ─────────────────────────────────────────────────

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("[MethodNotAllowed] {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ApiResponse.fail(ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    // ── Content-Type 불일치 ────────────────────────────────────────────────

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        log.warn("[UnsupportedMediaType] {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus())
                .body(ApiResponse.fail(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage()));
    }

    // ── 동시 수정 ────────────────────────────────────────────────
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLock() {
        return ResponseEntity
                .status(ErrorCode.REPORT_DUPLICATE.getStatus())
                .body(ApiResponse.fail(ErrorCode.REPORT_DUPLICATE.getMessage()));
    }

    // ── 예상치 못한 예외 (최후 방어선) ─────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("[Unexpected] {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR.getMessage()));
    }
}
