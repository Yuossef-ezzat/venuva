package com.example.aopmodule.aop.src.main.java.com.example.AOP.aspects;

import com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation.Cacheable;
import com.example.aopmodule.aop.src.main.java.com.example.AOP.cache.MethodResultCache;
import com.example.aopmodule.aop.src.main.java.com.example.AOP.support.CacheKeyResolution;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CachingAspect {

    private static final Logger logger = LoggerFactory.getLogger(CachingAspect.class);

    private final MethodResultCache methodResultCache;

    @Around("@annotation(cacheable)")
    public Object cache(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        String cacheKey = CacheKeyResolution.resolveKey(cacheable.key(), joinPoint);
        long cacheDurationMillis = cacheable.duration() * 1000;

        MethodResultCache.CacheEntry existing = methodResultCache.get(cacheKey);
        if (existing != null) {
            long ageMs = System.currentTimeMillis() - existing.timestamp;
            if (ageMs < cacheDurationMillis) {
                logger.info("From Cache: {} (age: {} ms)", cacheKey, ageMs);
                return existing.value;
            }
            methodResultCache.removeExact(cacheKey);
        }

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        methodResultCache.put(cacheKey, result, System.currentTimeMillis());
        logger.info("From Caching: {} ({} ms)", cacheKey, duration);

        return result;
    }
}
