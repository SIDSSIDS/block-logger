package com.github.sidssids.blocklogger.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import com.github.sidssids.blocklogger.layout.BaseBlockloggerPatternLayout;
import com.github.sidssids.blocklogger.layout.tools.Indent;

public abstract class BaseBlockloggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    
    protected       boolean profiling = true;
    protected final Indent  indent    = new Indent();

    protected abstract BaseBlockloggerPatternLayout createPatternLayout();
    
    public void setProfiling(Boolean profiling) {
        this.profiling = profiling;
    }
    
    @Override
    public void start() {
        BaseBlockloggerPatternLayout patternLayout = createPatternLayout();
        context.putObject(Indent.CONTEXT_KEY, indent);
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setProfiling(profiling);
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
    

}
