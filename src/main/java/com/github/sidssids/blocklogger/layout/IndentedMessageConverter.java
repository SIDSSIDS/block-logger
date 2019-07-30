package com.github.sidssids.blocklogger.layout;

import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class IndentedMessageConverter extends MessageConverter {
    
    @Override
    public void start() {
        Indent.getInstance().setEnabled(getContext().getProperty(Indent.Properties.ENABLED_PROPERTY));
        Indent.getInstance().setTabString(getContext().getProperty(Indent.Properties.TAB_STRING_PROPERTY));
        super.start();
    }
    
    @Override
    public String convert(ILoggingEvent event) {
        return new StringBuilder()
                .append(Indent.getInstance().get())
                .append(super.convert(event))
                .toString();
    }

}
