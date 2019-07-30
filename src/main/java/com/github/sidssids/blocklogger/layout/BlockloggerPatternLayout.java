package com.github.sidssids.blocklogger.layout;

import ch.qos.logback.classic.PatternLayout;
import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.sidssids.blocklogger.formatter.MarkerFormatter;
import com.github.sidssids.blocklogger.layout.tools.BlockLoggingEventProxy;
import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import org.slf4j.Marker;

public class BlockloggerPatternLayout extends PatternLayout {
    
    protected boolean profiling;

    public BlockloggerPatternLayout() {
        getInstanceConverterMap().put("m", IndentedMessageConverter.class.getName());
        getInstanceConverterMap().put("msg", IndentedMessageConverter.class.getName());
        getInstanceConverterMap().put("message", IndentedMessageConverter.class.getName());
    }
    
    private BlockLoggingEventProxy proxy(ILoggingEvent event) {
        return new BlockLoggingEventProxy(event, generateMessage(event));
    }
    
    public String generateMessage(ILoggingEvent event) {
        if (isOpening(event)) {
            return MarkerFormatter.generateOpenBlockMessage((StartMarker)event.getMarker());
        } else if (isClosing(event)) {
            return MarkerFormatter.generateCloseBlockMessage(profiling, (CloseMarker)event.getMarker());
        } else {
            return event.getMessage();
        }
    }
    
    @Override
    public String doLayout(ILoggingEvent event) {
        if (isOpeningOrClosingEvent(event)) {
            event = proxy(event);
        }
        if (isClosing(event)) {
            Indent.getInstance().decrement();
        }
        String result = super.doLayout(event);
        if (isOpening(event)) {
            Indent.getInstance().increment();
        }
        return result;
    }

    public void setProfiling(boolean profiling) {
        this.profiling = profiling;
    }
    
    private boolean isOpeningOrClosingEvent(ILoggingEvent event) {
        Marker m = event.getMarker();
        return m != null && (m instanceof StartMarker || m instanceof CloseMarker);
    }

    private boolean isOpening(ILoggingEvent event) {
        Marker m = event.getMarker();
        return m != null && m instanceof StartMarker;
    }
    
    private boolean isClosing(ILoggingEvent event) {
        Marker m = event.getMarker();
        return m != null && m instanceof CloseMarker;
    }
    
}
