package com.github.sidssids.blocklogger.layout.tools;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class BlockLoggingEventProxy extends IndentedLoggingEventProxy {
    
    private final String  message;
    private final boolean suppressException;

    public BlockLoggingEventProxy(ILoggingEvent event, Indent indent, String message, boolean suppressException) {
        super(event, indent);
        this.message           = message;
        this.suppressException = suppressException;
    }
    
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getFormattedMessage() {
        return message;
    }

    @Override
    public IThrowableProxy getThrowableProxy() {
        return suppressException ? null : event.getThrowableProxy();
    }

    @Override
    public StackTraceElement[] getCallerData() {
        return suppressException ? null : event.getCallerData();
    }

}
