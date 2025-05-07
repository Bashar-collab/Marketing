package com.custempmanag.marketing.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Pointcut("execution(* com.custempmanag.librarymanagmentsystem.service.*.*(..))")
    public void servicePointCut() {}

    @Before("servicePointCut()")
    public void beforeService(JoinPoint joinPoint) {
        logger.info("Executing before method: " + joinPoint.getSignature().getName());
    }

    @Around("servicePointCut()")
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Executing around method: " + joinPoint.getSignature().getName());

        Object result = joinPoint.proceed();

        logger.info("Method executed successfully: " + joinPoint.getSignature().getName());
        return result;
    }

    @After("servicePointCut()")
    public void afterService(JoinPoint joinPoint) {
        logger.info("Executing after method: " + joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "servicePointCut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        logger.severe("Method " + joinPoint.getSignature().getName() + " threw exception: " + exception.getMessage());
    }
}
