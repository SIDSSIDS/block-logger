package com.github.sidssids.blocklogger.layout;

import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class IndentedMessageConverter extends MessageConverter {
    
    @Override
    public String convert(ILoggingEvent event) {
        return new StringBuilder()
                .append(Indent.getInstance().get())
                .append(super.convert(event))
                .toString();
    }

}
