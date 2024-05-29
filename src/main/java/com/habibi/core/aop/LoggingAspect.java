package com.habibi.core.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);
    @Pointcut("execution(public * com.habibi.core.controller.*.*(..))")
    private void publicMethodOfControllerPackage() {
    }

    @Around(value = "publicMethodOfControllerPackage()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        logger.info("Entered in  >> {}() - {}", methodName, Arrays.toString(args));
        Object result = joinPoint.proceed();
        logger.info("Exited from >> {}() - {}", methodName, result);
        return result;
    }
}