package com.github.sidssids.blocklogger.layout;

import ch.qos.logback.classic.PatternLayout;
import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.sidssids.blocklogger.config.Settings;
import com.github.sidssids.blocklogger.formatter.MarkerFormatter;
import com.github.sidssids.blocklogger.layout.tools.BlockLoggingEventProxy;
import com.github.sidssids.blocklogger.layout.tools.IndentedLoggingEventProxy;
import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import org.slf4j.Marker;

public class BlockloggerPatternLayout extends PatternLayout {
    
    private final Settings settings;
    private final Indent   indent;

    public BlockloggerPatternLayout(Settings settings, Indent indent) {
        this.settings = settings;
        this.indent   = indent;
        getInstanceConverterMap().put("m", IndentedMessageConverter.class.getName());
        getInstanceConverterMap().put("msg", IndentedMessageConverter.class.getName());
        getInstanceConverterMap().put("message", IndentedMessageConverter.class.getName());
    }
    
    private ILoggingEvent proxy(ILoggingEvent event) {
        return new IndentedLoggingEventProxy(event, indent);
    }
    
    private ILoggingEvent startStopProxy(ILoggingEvent event) {
        return new BlockLoggingEventProxy(event, indent, generateMessage(event), suppressException(event.getMarker()));
    }
    
    private boolean suppressException(Marker marker) {
        return marker != null
                && marker instanceof CloseMarker
                && !CloseMarker.class.cast(marker)
                                        .getAppendStackTrace()
                                        .orElse(settings.isAppendStackTrace());
    }
    
    public String generateMessage(ILoggingEvent event) {
        if (isOpening(event)) {
            return MarkerFormatter.generateOpenBlockMessage(settings, (StartMarker)event.getMarker());
        } else if (isClosing(event)) {
            return MarkerFormatter.generateCloseBlockMessage(settings, (CloseMarker)event.getMarker());
        } else {
            return event.getMessage();
        }
    }
    
    @Override
    public String doLayout(ILoggingEvent event) {
        if (isOpeningOrClosingEvent(event)) {
            event = startStopProxy(event);
        } else {
            event = proxy(event);
        }
        if (isClosing(event)) {
            indent.decrement();
        }
        String result = super.doLayout(event);
        if (isOpening(event)) {
            indent.increment();
        }
        return result;
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
