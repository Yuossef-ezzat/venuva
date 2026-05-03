package com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation;

import java.lang.annotation.*;

/**
 * Removes entries from the method cache ({@link Cacheable}) after a successful method return.
 * If the return value is a failed {@code Result}, invalidation is skipped.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheInvalidate {

    String[] keys() default {};

    String[] keyPrefixes() default {};
}
