package com.kolychev.utils.blocklogger.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import com.kolychev.utils.blocklogger.layout.tools.Indent;

public abstract class BaseBlockloggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    
    protected final Indent indent = new Indent();

    protected abstract PatternLayout createPatternLayout();
    
    @Override
    public void start() {
        PatternLayout patternLayout = createPatternLayout();
        context.putObject(Indent.CONTEXT_KEY, indent);
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
    

}
