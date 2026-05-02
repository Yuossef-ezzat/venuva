package com.example.aopmodule.aop.src.main.java.com.example.AOP.Annotation;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HandleException {
    String value() default "An error occurred";
}