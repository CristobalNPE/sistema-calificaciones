package dev.cnpe.m6finalsc.application.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Object[] args = joinPoint.getArgs();
        log.info("🟢 -------------- Entrando al metodo [{}] en [{}]", methodName, className);
        log.info("Llamado con argumentos: {}", Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            if (result != null) {
                log.info("Retorno: {}", result.getClass().getSimpleName());
            }
            log.info("🔵 -------------- Saliendo del metodo: {}", methodName);

            return result;

        } catch (Exception e) {
            log.error("🔴 -------------- Exception en el metodo [{}] :{}", methodName, e.getMessage());
            throw e;
        }

    }

}
