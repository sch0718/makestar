package com.makestar.utils.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리를 담당하는 핸들러 클래스입니다.
 * Spring 애플리케이션에서 발생하는 다양한 예외들을 일관된 형식으로 처리하여
 * 클라이언트에게 적절한 에러 응답을 제공합니다.
 */
@Slf4j
public class GlobalExceptionHandler {

    /**
     * API 에러 응답을 위한 내부 정적 클래스입니다.
     * 에러의 타입, 메시지, 상세 정보를 포함하여 일관된 에러 응답 형식을 제공합니다.
     */
    public static class ApiError {
        private final String type;
        private final String message;
        private final Map<String, Object> details;

        /**
         * 기본 생성자 - 타입과 메시지만 포함하는 API 에러를 생성합니다.
         * 
         * @param type 에러 타입 (예: "NOT_FOUND", "VALIDATION_FAILED")
         * @param message 에러 메시지
         */
        public ApiError(String type, String message) {
            this.type = type;
            this.message = message;
            this.details = new HashMap<>();
        }

        /**
         * 상세 정보를 포함하는 API 에러를 생성합니다.
         * 
         * @param type 에러 타입
         * @param message 에러 메시지
         * @param details 추가적인 에러 상세 정보를 담은 Map
         */
        public ApiError(String type, String message, Map<String, Object> details) {
            this.type = type;
            this.message = message;
            this.details = details;
        }

        /**
         * 에러 타입을 반환합니다.
         * 
         * @return 에러 타입 문자열 (예: "NOT_FOUND", "VALIDATION_FAILED")
         */
        public String getType() {
            return type;
        }

        /**
         * 에러 메시지를 반환합니다.
         * 
         * @return 사용자가 이해할 수 있는 에러 메시지
         */
        public String getMessage() {
            return message;
        }

        /**
         * 에러와 관련된 상세 정보를 반환합니다.
         * 
         * @return 에러의 추가적인 상세 정보를 담은 Map
         */
        public Map<String, Object> getDetails() {
            return details;
        }
    }

    /**
     * ResourceNotFoundException 처리를 위한 핸들러 메서드입니다.
     * 요청한 리소스를 찾을 수 없는 경우 404 NOT_FOUND 응답을 반환합니다.
     * 
     * @param ex 발생한 ResourceNotFoundException 객체
     * @return NOT_FOUND(404) 상태 코드와 에러 정보를 포함한 ResponseEntity
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found exception: {}", ex.getMessage());
        ApiError error = new ApiError("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * 요청 데이터 유효성 검사 실패 처리를 위한 핸들러 메서드입니다.
     * Spring Validation이 실패한 경우 400 BAD_REQUEST 응답을 반환합니다.
     * 
     * @param ex 발생한 MethodArgumentNotValidException 객체
     * @return BAD_REQUEST(400) 상태 코드와 검증 실패 정보를 포함한 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation exception: {}", ex.getMessage());
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiError error = new ApiError("VALIDATION_FAILED", "Validation failed", errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 기타 예외 상황을 처리하기 위한 기본 핸들러 메서드입니다.
     * 처리되지 않은 모든 예외를 500 INTERNAL_SERVER_ERROR 응답으로 변환합니다.
     * 
     * @param ex 발생한 Exception 객체
     * @return INTERNAL_SERVER_ERROR(500) 상태 코드와 에러 정보를 포함한 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
        ApiError error = new ApiError("SERVER_ERROR", "An unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 