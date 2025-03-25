package com.makestar.utils.exception;

/**
 * 요청한 리소스를 찾을 수 없을 때 발생하는 예외 클래스입니다.
 * 주로 데이터베이스나 파일 시스템에서 특정 ID나 키로 리소스를 조회할 때,
 * 해당 리소스가 존재하지 않는 경우 사용됩니다.
 * 
 * <p>이 예외는 RuntimeException을 상속받아 Unchecked Exception으로 구현되었습니다.</p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 지정된 오류 메시지로 새로운 ResourceNotFoundException을 생성합니다.
     * 
     * @param message 예외에 대한 상세 메시지
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 리소스 정보를 포함한 상세한 오류 메시지로 새로운 ResourceNotFoundException을 생성합니다.
     * 
     * @param resourceName 찾을 수 없는 리소스의 이름 (예: "User", "Post" 등)
     * @param fieldName 검색에 사용된 필드 이름 (예: "id", "email" 등)
     * @param fieldValue 검색에 사용된 필드 값
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
} 