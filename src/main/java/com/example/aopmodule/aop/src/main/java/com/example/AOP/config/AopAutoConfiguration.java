package com.example.aopmodule.aop.src.main.java.com.example.AOP.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnProperty(name = "aop.enabled", havingValue = "true", matchIfMissing = true)
public class AopAutoConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(AopAutoConfiguration.class);

    public AopAutoConfiguration() {
        logger.info(" AOP Auto Configuration ");
        logger.info("   - LoggingAspect");
        logger.info("   - ExceptionHandlingAspect");
        logger.info("   - CacheInvalidateAspect");
        logger.info("   - SecurityAspect");
        logger.info("   - CachingAspect");
        logger.info("   - PerformanceMonitoringAspect");
        logger.info("   - AuditLoggingAspect");
    }

}