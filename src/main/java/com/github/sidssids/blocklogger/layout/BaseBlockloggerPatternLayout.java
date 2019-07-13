package com.github.sidssids.blocklogger.layout;

import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.github.sidssids.blocklogger.layout.tools.LoggingEventCloner;
import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import java.time.Duration;
import java.util.Optional;
import org.slf4j.Marker;

public abstract class BaseBlockloggerPatternLayout extends PatternLayout {
    
    protected final Indent  indent;
    protected       boolean profiling;

    public BaseBlockloggerPatternLayout(Indent indent) {
        this.indent = indent;
        
        getInstanceConverterMap().put("m", BlockConverter.class.getName());
        getInstanceConverterMap().put("msg", BlockConverter.class.getName());
        getInstanceConverterMap().put("message", BlockConverter.class.getName());
    }

    public void setProfiling(boolean profiling) {
        this.profiling = profiling;
    }

    public boolean isProfiling() {
        return profiling;
    }
    
    protected boolean isOpening(ILoggingEvent event) {
        Marker m = event.getMarker();
        return m != null && m instanceof StartMarker;
    }
    
    protected boolean isClosing(ILoggingEvent event) {
        Marker m = event.getMarker();
        return m != null && m instanceof CloseMarker;
    }
    
    protected ILoggingEvent generateOpenBlockEvent(ILoggingEvent openEvent) {
        return generateOpenBlockEvent(openEvent, Optional.empty());
    }
    
    protected ILoggingEvent generateOpenBlockEvent(ILoggingEvent openEvent, Optional<ILoggingEvent> nextEvent) {
        boolean         emptyBlock = isEmptyBlock(nextEvent);
        Level           level      = emptyBlock ? nextEvent.get().getLevel() : openEvent.getLevel();
        long            timestamp  = nextEvent.isPresent() ? nextEvent.get().getTimeStamp() : openEvent.getTimeStamp();
        String          message    = generateOpenBlockMessage(openEvent, nextEvent);
        IThrowableProxy throwable  = emptyBlock ? nextEvent.get().getThrowableProxy() : openEvent.getThrowableProxy();
        
        return LoggingEventCloner.clone(openEvent, level, message, timestamp, throwable);
    }
    
    protected ILoggingEvent generateCloseBlockEvent(ILoggingEvent event) {
        String msg = generateCloseBlockMessage(event);
        return LoggingEventCloner.clone(event, msg);
    }
    
    private boolean isEmptyBlock(Optional<ILoggingEvent> nextEvent) {
        return nextEvent.isPresent() && isClosing(nextEvent.get());
    }
    
    private String generateOpenBlockMessage(ILoggingEvent openEvent, Optional<ILoggingEvent> nextEvent) {
        boolean       emptyBlock = isEmptyBlock(nextEvent);
        StartMarker   marker     = StartMarker.class.cast(openEvent.getMarker());
        StringBuilder message    = new StringBuilder();
        
        if (!emptyBlock) {
            message.append("[+] ");
        }
        message.append(marker.getTitle());
        if (emptyBlock && profiling) {
            message.append(" (").append(getDuration(nextEvent.get()).toString()).append(")");
        }
        if (marker.getParams().isPresent()) {
            message.append(" (").append(marker.getParams().get()).append(")");
        }
        if (emptyBlock) {
            String closeMsg = generateCloseBlockResult(nextEvent.get());
            if (closeMsg.length() > 0) {
                message.append(": ").append(closeMsg);
            }
        } else {
            message.append(": Started...");
        }
        
        return message.toString();
    }
    
    private String generateCloseBlockMessage(ILoggingEvent closeEvent) {
        CloseMarker   marker  = CloseMarker.class.cast(closeEvent.getMarker());
        StringBuilder message = new StringBuilder();
        
        message.append("[-] ").append(marker.getTitle());
        if (profiling) {
            message.append(" (").append(marker.getDuration().toString()).append(")");
        }
        String closeMsg = generateCloseBlockResult(closeEvent);
        if (closeMsg.length() > 0) {
            message.append(": ").append(closeMsg);
        }
        
        return message.toString();
    }
    
    private Duration getDuration(ILoggingEvent closeEvent) {
        return CloseMarker.class.cast(closeEvent.getMarker()).getDuration();
    }
    
    private String generateCloseBlockResult(ILoggingEvent closeEvent) {
        CloseMarker   marker  = CloseMarker.class.cast(closeEvent.getMarker());
        StringBuilder message = new StringBuilder();
        if (marker.getResult().isPresent()) {
            message.append("Result - ").append(marker.getResult().get());
        }
        if (marker.getException().isPresent()) {
            if (message.length() > 0) {
                message.append("; ");
            }
            message.append("Exception: ").append(marker.getException().get().toString());
        }
        return message.toString();
    }
    
}
