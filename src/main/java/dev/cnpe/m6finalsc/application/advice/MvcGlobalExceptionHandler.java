package dev.cnpe.m6finalsc.application.advice;

import dev.cnpe.m6finalsc.domain.DomainException;
import dev.cnpe.m6finalsc.domain.DomainException.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.CONSTRAINT_VIOLATION;
import static dev.cnpe.m6finalsc.domain.DomainException.ErrorCode.GENERAL;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
@Order(LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class MvcGlobalExceptionHandler {

    private final MessageSource messageSource;
    private final Set<Class<? extends Exception>> exceptionsToRethrow = Set.of(
            AccessDeniedException.class
    );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView onValidationException(MethodArgumentNotValidException ex) {
        List<String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                                          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                                          .toList();

        return handleException(CONSTRAINT_VIOLATION, ex, validationErrors);
    }

    @ExceptionHandler(DomainException.class)
    public ModelAndView onDomainException(DomainException ex) {
        return handleException(ex.getErrorCode(), ex, null);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView onAnyOtherException(Exception exception) throws Exception {
        if (shouldRethrowException(exception)) {
            throw exception;
        }
        return handleException(GENERAL, exception, null);
    }

    private boolean shouldRethrowException(Exception exception) {
        return exceptionsToRethrow.stream()
                                  .anyMatch(exceptionClass -> exceptionClass.isInstance(exception));
    }

    private ModelAndView handleException(ErrorCode errorCode,
                                         Exception exception,
                                         List<String> validationErrors) {
        ModelAndView mav = new ModelAndView("error");
        String userMessage = messageSource.getMessage(
                "error." + errorCode,
                exception instanceof DomainException de ? de.getParameters() : null, "Internal Error",
                getLocale());

        logError(errorCode, exception, userMessage);

        mav.addObject("message", userMessage);
        mav.addObject("type", "danger");
        if (validationErrors != null) {
            mav.addObject("errors", validationErrors);
        }
        mav.addObject("timestamp", java.time.LocalTime.now());
        mav.addObject("errorCode", errorCode.name());

        return mav;
    }


    private void logError(ErrorCode errorCode, Exception exception, String userMessage) {
        log.error("⚠ Error {}: {}", errorCode, userMessage);
        log.debug("Stack trace below:", exception);
    }

}