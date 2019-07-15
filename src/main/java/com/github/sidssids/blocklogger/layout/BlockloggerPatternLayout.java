package com.github.sidssids.blocklogger.layout;

import ch.qos.logback.classic.PatternLayout;
import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import org.slf4j.Marker;

public class BlockloggerPatternLayout extends PatternLayout {
    
    protected boolean profiling;
    private final Indent  indent;

    public BlockloggerPatternLayout(Indent indent) {
        this.indent = indent;
        
        getInstanceConverterMap().put("m", BlockConverter.class.getName());
        getInstanceConverterMap().put("msg", BlockConverter.class.getName());
        getInstanceConverterMap().put("message", BlockConverter.class.getName());
    }
    
    @Override
    public String doLayout(ILoggingEvent event) {
        if (isClosing(event)) {
            indent.decrement();
        }
        String result = super.doLayout(event);
        if (isOpening(event)) {
            indent.increment();
        }
        return result;
    }

    public void setProfiling(boolean profiling) {
        this.profiling = profiling;
    }

    public boolean isProfiling() {
        return profiling;
    }
    
    private boolean isOpening(ILoggingEvent event) {
        Marker m = event.getMarker();
        return m != null && m instanceof StartMarker;
    }
    
    private boolean isClosing(ILoggingEvent event) {
        Marker m = event.getMarker();
        return m != null && m instanceof CloseMarker;
    }
    
    public Indent getIndent() {
        return indent;
    }

}
