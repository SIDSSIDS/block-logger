package com.github.sidssids.blocklogger.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.slf4j.event.Level;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface BlockLoggable {
    
    public String[] value() default {};
    
    public String[] argNames() default {};

    public String loggerName() default "";
    
    public Class loggerClass() default Object.class;
    
    public String title() default "";
    
    public boolean appendClassName() default false;
    
    public boolean appendArgs() default true;
    
    public boolean appendResult() default true;
    
    public boolean appendExceptionInfo() default true;
    
    public boolean appendStackTrace() default true;
    
    public Class<? extends Throwable>[] ignoreExceptions() default {};
    
    public Level level() default Level.INFO;
    
}
