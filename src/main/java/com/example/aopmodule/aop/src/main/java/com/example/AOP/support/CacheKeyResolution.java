package com.example.aopmodule.aop.src.main.java.com.example.AOP.support;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CacheKeyResolution {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^}]+)}");
    private static final ParameterNameDiscoverer NAME_DISCOVERY = new DefaultParameterNameDiscoverer();

    private CacheKeyResolution() {
    }

    public static String resolveKey(String template, ProceedingJoinPoint joinPoint) {
        if (template == null || template.isEmpty()) {
            return defaultCacheKey(joinPoint);
        }
        if (!template.contains("{")) {
            return template;
        }
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        Method method = sig.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] names = sig.getParameterNames();
        if (names == null || names.length != args.length) {
            names = NAME_DISCOVERY.getParameterNames(method);
        }
        if (names == null || names.length != args.length) {
            names = fallbackArgNames(args.length);
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            String token = matcher.group(1).trim();
            String replacement = resolveToken(token, names, args);
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    public static String defaultCacheKey(ProceedingJoinPoint joinPoint) {
        String signature = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "#" + joinPoint.getSignature().getName();
        return signature + "(" + Arrays.deepHashCode(joinPoint.getArgs()) + ")";
    }

    private static String[] fallbackArgNames(int length) {
        String[] names = new String[length];
        for (int i = 0; i < length; i++) {
            names[i] = "arg" + i;
        }
        return names;
    }

    private static String resolveToken(String token, String[] parameterNames, Object[] args) {
        if (parameterNames.length == 0 || args.length == 0) {
            return "";
        }
        if (token.matches("^\\d+$")) {
            int idx = Integer.parseInt(token);
            if (idx >= 0 && idx < args.length && args[idx] != null) {
                return args[idx].toString();
            }
            return "";
        }
        for (int i = 0; i < parameterNames.length && i < args.length; i++) {
            if (token.equals(parameterNames[i]) && args[i] != null) {
                return args[i].toString();
            }
        }
        return "";
    }
}
