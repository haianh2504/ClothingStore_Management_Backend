package org.example.spring_backend_clothingstore.exception.business;

public abstract class BusinessException extends RuntimeException {
    private final String code;

    protected BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    protected BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
