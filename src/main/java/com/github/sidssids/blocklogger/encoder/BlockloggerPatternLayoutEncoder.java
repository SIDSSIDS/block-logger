package com.github.sidssids.blocklogger.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import com.github.sidssids.blocklogger.config.Settings;
import com.github.sidssids.blocklogger.layout.BlockloggerPatternLayout;

public class BlockloggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent>  {
    
    private final Settings settings = new Settings();
    
    public void setProfiling(Boolean profiling) {
        settings.setProfiling(profiling);
    }

    public void setAppendParams(Boolean appendParams) {
        settings.setAppendParams(appendParams);
    }

    public void setAppendResult(Boolean appendResult) {
        settings.setAppendResult(appendResult);
    }

    public void setAppendExceptionInfo(Boolean appendExceptionInfo) {
        settings.setAppendExceptionInfo(appendExceptionInfo);
    }

    public void setAppendStackTrace(Boolean appendStackTrace) {
        settings.setAppendStackTrace(appendStackTrace);
    }

    public Settings getSettings() {
        return settings;
    }
    
    @Override
    public void start() {
        BlockloggerPatternLayout patternLayout = new BlockloggerPatternLayout(settings);
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

}
