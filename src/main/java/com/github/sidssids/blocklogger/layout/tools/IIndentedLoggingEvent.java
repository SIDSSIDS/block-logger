package com.github.sidssids.blocklogger.layout.tools;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface IIndentedLoggingEvent extends ILoggingEvent {

    Indent getIndent();
    
}
