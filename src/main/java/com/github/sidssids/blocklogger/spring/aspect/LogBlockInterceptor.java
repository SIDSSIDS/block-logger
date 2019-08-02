package com.github.sidssids.blocklogger.spring.aspect;

import com.github.sidssids.blocklogger.logger.LogBlock;
import com.github.sidssids.blocklogger.logger.LogBlockFactory;
import com.github.sidssids.blocklogger.spring.annotation.BlockLoggable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogBlockInterceptor {

    @Pointcut("execution(public * *(..))")
    protected void publicMethod() {
    }
    
    @Pointcut("@annotation(blockLoggable)")
    protected void loggableMethod(BlockLoggable blockLoggable) {
    }
    
    @Around(value = "publicMethod() && loggableMethod(blockLoggable)", argNames = "blockLoggable")
    public Object logMethod(ProceedingJoinPoint joinPoint, BlockLoggable blockLoggable) throws Throwable {
        Class  clazz      = joinPoint.getSignature().getDeclaringType();
        String blockName  = getBlockName(joinPoint, blockLoggable);
        String params     = createArgs(blockLoggable, joinPoint.getArgs());
        Level  level      = blockLoggable.level();
        try (LogBlock log = LogBlockFactory.create(clazz, level, blockName, params)) {
            try {
                Object result = joinPoint.proceed();
                if (blockLoggable.appendResult()) {
                    Class returnClass = MethodSignature.class.cast(joinPoint.getSignature()).getReturnType();
                    if (!Void.TYPE.equals(returnClass) && !Void.class.equals(returnClass)) {
                        log.reportSuccess("%s", formatValue(result));
                    }
                }
                return result;
            } catch (Throwable e) {
                if (process(blockLoggable, e)) {
                    log.withException(e, blockLoggable.appendExceptionInfo(), blockLoggable.appendStackTrace()).reportError();
                }
                throw e;
            }
        }
    }
    
    private boolean process(BlockLoggable blockLoggable, Throwable e) {
        for (Class<? extends Throwable> ignoreException : blockLoggable.ignoreExceptions()) {
            if (ignoreException.equals(e.getClass())) {
                return false;
            }
        }
        return true;
    }
    
    private String createArgs(BlockLoggable blockLoggable, Object[] args) {
        if (!blockLoggable.appendArgs() || args == null || args.length == 0) {
            return null;
        }
        String[] argNames = getArgNames(blockLoggable);
        if (argNames.length == 0) {
            return String.format("%s", Arrays.asList(args));
        } else {
            return IntStream
                    .range(0, argNames.length)
                    .mapToObj(i -> createArg(i, argNames, args))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }
    }
    
    private String[] getArgNames(BlockLoggable blockLoggable) {
        if (blockLoggable.value().length > 0) {
            return blockLoggable.value();
        } else {
            return blockLoggable.argNames();
        }
    }
    
    private String createArg(int index, String[] argNames, Object[] args) {
        String name = argNames[index];
        if (name == null || "".equals(name) || args.length <= index) {
            return null;
        }
        Object value    = args[index];
        String valueStr = formatValue(value);
        return String.format("%s=%s", name, valueStr);
    }
    
    private String formatValue(Object value) {
        if (value != null
                && value.getClass().isArray()) {
            if (Byte.TYPE.equals(value.getClass().getComponentType())) {
                return String.valueOf(byte[].class.cast(value).length);
            } else if (Boolean.TYPE.equals(value.getClass().getComponentType())) {
                return Arrays.toString(boolean[].class.cast(value));
            } else if (Character.TYPE.equals(value.getClass().getComponentType())) {
                return Arrays.toString(char[].class.cast(value));
            } else if (Short.TYPE.equals(value.getClass().getComponentType())) {
                return Arrays.toString(short[].class.cast(value));
            } else if (Integer.TYPE.equals(value.getClass().getComponentType())) {
                return Arrays.toString(int[].class.cast(value));
            } else if (Long.TYPE.equals(value.getClass().getComponentType())) {
                return Arrays.toString(long[].class.cast(value));
            } else if (Float.TYPE.equals(value.getClass().getComponentType())) {
                return Arrays.toString(float[].class.cast(value));
            } else if (Double.TYPE.equals(value.getClass().getComponentType())) {
                return Arrays.toString(double[].class.cast(value));
            } else {
                return Arrays.toString(Object[].class.cast(value));
            }
        } else {
            return String.valueOf(value);
        }
    }
    
    private String getBlockName(ProceedingJoinPoint joinPoint, BlockLoggable blockLoggable) {
        String methodName;
        if (blockLoggable.title() != null && !"".equals(blockLoggable.title())) {
            methodName = blockLoggable.title();
        } else {
            methodName = joinPoint.getSignature().getName();
        }
        if (blockLoggable.appendClassName()) {
            String className  = joinPoint.getSignature().getDeclaringType().getSimpleName();
            return String.format("%s.%s", className, methodName);
        } else {
            return methodName;
        }
    }

}
