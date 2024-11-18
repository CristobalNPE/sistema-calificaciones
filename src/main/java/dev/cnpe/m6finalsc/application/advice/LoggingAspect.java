package dev.cnpe.m6finalsc.application.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("httpMethods() && restControllers()")
    public Object logHttpMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info("🌐 Peticion HTTP {} a {}",
                request.getMethod(),
                request.getRequestURI());

        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            logMethodEntry(joinPoint, methodName, className);
            Object result = joinPoint.proceed();
            logMethodExit(methodName, result, startTime);
            return result;
        } catch (Exception e) {
            logError(methodName, e);
            throw e;
        }
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void httpMethods() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllers() {
    }

    private void logMethodEntry(ProceedingJoinPoint joinPoint, String methodName, String className) {
        Object[] args = joinPoint.getArgs();
        log.info("🟢 Iniciando metodo: [{}] en [{}]", methodName, className);

        if (args.length > 0) {
            Arrays.stream(args)
                  .filter(Objects::nonNull)
                  .forEach(arg -> log.info("Parametro: {} = {}",
                          arg.getClass().getSimpleName(),
                          sanitizeArgument(arg)));
        }
    }

    private void logMethodExit(String methodName, Object result, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;
        if (result != null) {
            log.info("Retorno: {} = {}",
                    result.getClass().getSimpleName(),
                    sanitizeArgument(result));
        }
        log.info("🔵 Finalizando metodo: {} ({}ms)", methodName, executionTime);
    }

    private void logError(String methodName, Exception e) {
        log.error("🔴 Excepcion en el metodo [{}]", methodName);
        log.error("Detalles del error: ", e);
    }

    private String sanitizeArgument(Object arg) {

        if (arg instanceof Page<?> page) {
            return String.format("Pagina %d de %d con %d elemento(s)",
                    page.getNumber() + 1,
                    page.getTotalPages(),
                    page.getNumberOfElements());
        }
        if (arg instanceof Collection<?>) {
            return "Coleccion<" + ((Collection<?>) arg).size() + " elemento(s)>";
        }
        if (arg.toString().length() > 100) {
            return arg.toString().substring(0, 100) + "...";
        }
        return arg.toString();
    }

}
