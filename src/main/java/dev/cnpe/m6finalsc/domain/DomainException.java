package dev.cnpe.m6finalsc.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String[] parameters;

    public DomainException(ErrorCode errorCode, String... parameters) {
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public DomainException(Throwable cause) {
        this(cause, ErrorCode.GENERAL);
    }

    public DomainException(String message, ErrorCode errorCode, String... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }


    public DomainException(String message, Throwable cause, ErrorCode errorCode, String... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public DomainException(Throwable cause, ErrorCode errorCode, String... parameters) {
        super(cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public DomainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode, String... parameters) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public enum ErrorCode {
        GENERAL,
        CONSTRAINT_VIOLATION(BAD_REQUEST),
        DATA_TYPE_MISMATCH(BAD_REQUEST),
        RESOURCE_NOT_FOUND(NOT_FOUND),
        DUPLICATED_DATA(CONFLICT),
        INVALID_DATA(BAD_REQUEST);

        public final HttpStatus statusCode;

        ErrorCode() {
            this(INTERNAL_SERVER_ERROR);
        }

        ErrorCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
        }
    }
}
