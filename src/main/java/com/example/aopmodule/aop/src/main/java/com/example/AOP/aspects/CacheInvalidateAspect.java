package com.example.aopmodule.aop.src.main.java.com.example.AOP.aspects;

import com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation.CacheInvalidate;
import com.example.aopmodule.aop.src.main.java.com.example.AOP.cache.MethodResultCache;
import com.example.venuva.Core.Domain.Abstractions.Result;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CacheInvalidateAspect {

    private static final Logger logger = LoggerFactory.getLogger(CacheInvalidateAspect.class);

    private final MethodResultCache methodResultCache;

    @AfterReturning(value = "@annotation(invalidate)", returning = "ret")
    public void invalidateAfterSuccess(JoinPoint joinPoint, CacheInvalidate invalidate, Object ret) {
        if (invalidate.keys().length == 0 && invalidate.keyPrefixes().length == 0) {
            return;
        }
        if (ret instanceof Result<?> result && result.isFailure()) {
            logger.debug("Skip cache invalidation (failed result): {}.{}", 
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName());
            return;
        }
        methodResultCache.invalidate(invalidate.keys(), invalidate.keyPrefixes());
        logger.info("Cache invalidated after {}.{}", 
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }
}
