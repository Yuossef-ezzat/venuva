package com.example.aopmodule.aop.src.main.java.com.example.AOP.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation.Loggable;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


    @Around("@annotation(loggable)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        logger.info(" بدء تنفيذ: {}.{}", className, methodName);
        
        if (loggable.logArguments()) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                logger.debug(" المدخلات: {}", (Object) args);
            }
        }

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

            logger.info("اكتمل بنجاح: {}.{} في {} ms", className, methodName, stopWatch.getLastTaskTimeMillis());
            
            if (loggable.logResult() && result != null) {
                logger.debug("النتيجة: {}", result);
            }
            return result;

        } catch (Throwable throwable) {
            stopWatch.stop();
            logger.error(" خطأ في: {}.{} بعد {} ms",
                    className, methodName, stopWatch.getLastTaskTimeMillis(), throwable);
            throw throwable;
        }
    }
}