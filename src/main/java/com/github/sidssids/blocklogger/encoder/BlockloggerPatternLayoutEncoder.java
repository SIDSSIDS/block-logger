package com.github.sidssids.blocklogger.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import com.github.sidssids.blocklogger.layout.BlockloggerPatternLayout;

public class BlockloggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent>  {
    
    protected boolean profiling = true;

    public void setProfiling(Boolean profiling) {
        this.profiling = profiling;
    }    
    
    @Override
    public void start() {
        BlockloggerPatternLayout patternLayout = new BlockloggerPatternLayout();
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setProfiling(profiling);
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

}
