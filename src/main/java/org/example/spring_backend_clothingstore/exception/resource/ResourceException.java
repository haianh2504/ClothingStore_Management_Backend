package org.example.spring_backend_clothingstore.exception.resource;

public abstract class ResourceException extends RuntimeException {
    private final String code;

    protected ResourceException(String code, String message) {
        super(message);
        this.code = code;
    }

    protected ResourceException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
