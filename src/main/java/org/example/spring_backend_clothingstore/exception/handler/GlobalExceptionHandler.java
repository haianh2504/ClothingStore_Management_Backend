package org.example.spring_backend_clothingstore.exception.handler;

import org.example.spring_backend_clothingstore.exception.business.BusinessException;
import org.example.spring_backend_clothingstore.exception.resource.ResourceException;
import org.example.spring_backend_clothingstore.exception.response.ErrorResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceException.class, BusinessException.class})
    public ResponseEntity<ErrorResponse> handleApplicationException(RuntimeException exception) {
        HttpStatus status = resolveStatus(exception);
        String code = resolveCode(exception);

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.from(status.value(), code, exception.getMessage()));
    }

    @ExceptionHandler(org.example.spring_backend_clothingstore.exception.business.ProductUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleProductUnavailable(
            org.example.spring_backend_clothingstore.exception.business.ProductUnavailableException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.from(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        exception.getCode(),
                        exception.getMessage(),
                        exception.getErrors()
                ));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeader(
            MissingRequestHeaderException exception
    ) {
        String code = "MISSING_REQUEST_HEADER";
        String message = "Required header '" + exception.getHeaderName() + "' is missing";

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.from(400, code, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        List<Map<String, String>> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.from(
                        400,
                        "INVALID_INFORMATION_PROVIDED",
                        "Failed: Invalid details provided",
                        errors
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.from(
                        HttpStatus.UNAUTHORIZED.value(),
                        "INVALID_CREDENTIALS",
                        "Invalid email or password"
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException exception
    ) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.from(
                        status.value(),
                        "HTTP_ERROR",
                        exception.getReason()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred"
                ));
    }

    private HttpStatus resolveStatus(RuntimeException exception) {
        org.springframework.web.bind.annotation.ResponseStatus responseStatus =
                AnnotationUtils.findAnnotation(exception.getClass(),
                        org.springframework.web.bind.annotation.ResponseStatus.class);

        if (responseStatus != null) {
            return responseStatus.value();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveCode(RuntimeException exception) {
        if (exception instanceof ResourceException resourceException) {
            return resourceException.getCode();
        }

        if (exception instanceof BusinessException businessException) {
            return businessException.getCode();
        }

        return "INTERNAL_SERVER_ERROR";
    }
}
