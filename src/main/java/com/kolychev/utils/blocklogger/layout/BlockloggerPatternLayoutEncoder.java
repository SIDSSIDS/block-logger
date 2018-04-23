package com.kolychev.utils.blocklogger.layout;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

public class BlockloggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    
    @Override
    public void start() {
        BlockloggerPatternLayout patternLayout = new BlockloggerPatternLayout();
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
    
}
