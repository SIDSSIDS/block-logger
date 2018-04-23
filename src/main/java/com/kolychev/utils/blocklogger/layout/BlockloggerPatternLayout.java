package com.kolychev.utils.blocklogger.layout;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.kolychev.utils.blocklogger.logger.markers.CloseMarker;
import com.kolychev.utils.blocklogger.logger.markers.StartMarker;
import org.slf4j.Marker;

public class BlockloggerPatternLayout extends PatternLayout {

    private final Indent indent;
    
    public BlockloggerPatternLayout(Indent indent) {
        this.indent = indent;
        
        getInstanceConverterMap().put("m", BlockConverter.class.getName());
        getInstanceConverterMap().put("msg", BlockConverter.class.getName());
        getInstanceConverterMap().put("message", BlockConverter.class.getName());
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker != null && marker instanceof CloseMarker) {
            indent.decrement();
        }
        String result = super.doLayout(event);
        if (marker != null && marker instanceof StartMarker) {
            indent.increment();
        }
        return result;
    }

}
