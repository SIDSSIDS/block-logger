package com.github.sidssids.blocklogger.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import com.github.sidssids.blocklogger.layout.BlockloggerPatternLayout;
import com.github.sidssids.blocklogger.layout.tools.Indent;

public class BlockloggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent>  {
    
    protected       boolean profiling = true;
    protected final Indent  indent    = new Indent();

    protected BlockloggerPatternLayout createPatternLayout() {
        return new BlockloggerPatternLayout(indent);
    }
    
    public void setProfiling(Boolean profiling) {
        this.profiling = profiling;
    }
    
    public void setTabString(String tabString) {
        indent.setTabString(tabString);
    }
    
    @Override
    public void start() {
        BlockloggerPatternLayout patternLayout = createPatternLayout();
        context.putObject(Indent.CONTEXT_KEY, indent);
        context.putObject(Indent.PROFILING_KEY, profiling);
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setProfiling(profiling);
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

}
