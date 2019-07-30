package com.github.sidssids.blocklogger.config;

public class Settings implements Cloneable {
    
    public static class Defaults {
        public static final boolean  PROFILING             = true;
        public static final boolean  APPEND_PARAMS         = true;
        public static final boolean  APPEND_RESULT         = true;
        public static final boolean  APPEND_EXCEPTION_INFO = true;
        public static final boolean  APPEND_STACK_TRACE    = true;
        public static final Settings SETTINGS              = new Settings();
    }
    
    private boolean profiling           = Defaults.PROFILING;
    private boolean appendParams        = Defaults.APPEND_PARAMS;
    private boolean appendResult        = Defaults.APPEND_RESULT;
    private boolean appendExceptionInfo = Defaults.APPEND_EXCEPTION_INFO;
    private boolean appendStackTrace    = Defaults.APPEND_STACK_TRACE;

    public Settings() {
    }

    public Settings(
            boolean profiling,
            boolean appendParams,
            boolean appendResult,
            boolean appendExceptionInfo,
            boolean appendStackTrace) {
        this.profiling = profiling;
        this.appendParams = appendParams;
        this.appendResult = appendResult;
        this.appendExceptionInfo = appendExceptionInfo;
        this.appendStackTrace = appendStackTrace;
    }

    private <T> T isNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
    
    public boolean isProfiling() {
        return profiling;
    }

    public void setProfiling(Boolean profiling) {
        this.profiling = isNull(profiling, Defaults.PROFILING);
    }
    
    public boolean isAppendParams() {
        return appendParams;
    }

    public void setAppendParams(Boolean appendParams) {
        this.appendParams = isNull(appendParams, Defaults.APPEND_PARAMS);
    }

    public boolean isAppendResult() {
        return appendResult;
    }

    public void setAppendResult(Boolean appendResult) {
        this.appendResult = isNull(appendResult, Defaults.APPEND_RESULT);
    }

    public boolean isAppendExceptionInfo() {
        return appendExceptionInfo;
    }

    public void setAppendExceptionInfo(Boolean appendExceptionInfo) {
        this.appendExceptionInfo = isNull(appendExceptionInfo, Defaults.APPEND_EXCEPTION_INFO);
    }

    public boolean isAppendStackTrace() {
        return appendStackTrace;
    }

    public void setAppendStackTrace(Boolean appendStackTrace) {
        this.appendStackTrace = isNull(appendStackTrace, Defaults.APPEND_STACK_TRACE);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Settings(
                profiling,
                appendParams,
                appendResult,
                appendExceptionInfo,
                appendStackTrace
        );
    }

}
