package com.kolychev.utils.blocklogger.layout;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

public class BlockloggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    
    private final Indent indent = new Indent();
    
    @Override
    public void start() {
        BlockloggerPatternLayout patternLayout = new BlockloggerPatternLayout(indent);
        context.putObject(Indent.CONTEXT_KEY, indent);
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
    
}
