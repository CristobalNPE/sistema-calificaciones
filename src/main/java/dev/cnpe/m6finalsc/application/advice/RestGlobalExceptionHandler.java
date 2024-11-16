package dev.cnpe.m6finalsc.application.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import dev.cnpe.m6finalsc.domain.DomainException;
import dev.cnpe.m6finalsc.domain.DomainException.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.*;
import static java.time.LocalTime.now;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Order(HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RestGlobalExceptionHandler {

    private final MessageSource messageSource;

    private final Set<Class<? extends Exception>> exceptionsToRethrow = Set.of(
            AccessDeniedException.class,
            BadCredentialsException.class
//            UsernameNotFoundException.class
    );
    @Value("${app.error.base-uri}")
    private String errorTypeBase;


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail onHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        if (ex.getCause() instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().getFirst().getFieldName();
            String invalidValue = ife.getValue().toString();
            return handleException(DATA_TYPE_MISMATCH, ex, invalidValue, fieldName);
        }
        return handleException(GENERAL, ex);
    }

    // @Valid
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail onJavaxValidationException(MethodArgumentNotValidException ex) {
        List<String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                                          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                                          .toList();

        ProblemDetail pd = handleException(CONSTRAINT_VIOLATION, ex);
        pd.setProperty("errors", validationErrors);
        pd.setProperty("errorsCount", validationErrors.size());

        return pd;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail onUserException(DomainException ex) {
        return handleException(
                ex.getErrorCode(),
                ex,
                ex.getParameters()
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onAnyOtherException(Exception exception) throws Exception {

        if (shouldRethrowException(exception)) {
            throw exception;
        }
        return handleException(GENERAL, exception);
    }

    private boolean shouldRethrowException(Exception exception) {
        return exceptionsToRethrow.stream()
                                  .anyMatch(exceptionClass -> exceptionClass.isInstance(exception));
    }

    private ProblemDetail handleException(ErrorCode errorCode,
                                          Exception exception,
                                          String... messageParameters) {

        String userMessage = messageSource.getMessage(
                "error." + errorCode,
                messageParameters, "Internal Error",
                getLocale());

        logError(errorCode, exception, userMessage);

        ProblemDetail pd = ProblemDetail.forStatus(errorCode.statusCode);
        pd.setType(formatUriForErrorCode(errorCode.name()));
        pd.setDetail(userMessage);
        pd.setTitle(errorCode.name());
        pd.setProperty("timestamp", now());
        return pd;
    }

    private void logError(ErrorCode errorCode, Exception exception, String userMessage) {
        log.error("⚠ Error {}: {}", errorCode, userMessage);
        log.debug("Stack trace bellow:", exception);
    }

    private URI formatUriForErrorCode(String errorCode) {
        return URI.create(errorTypeBase + errorCode
                .toLowerCase()
                .replace('_', '-'));
    }

}
