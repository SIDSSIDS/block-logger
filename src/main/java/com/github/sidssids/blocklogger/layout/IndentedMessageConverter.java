package com.github.sidssids.blocklogger.layout;

import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.sidssids.blocklogger.layout.tools.IIndentedLoggingEvent;

public class IndentedMessageConverter extends MessageConverter {
    
    @Override
    public String convert(ILoggingEvent event) {
        
        String converted = super.convert(event);
        if (IIndentedLoggingEvent.class.isAssignableFrom(event.getClass())) {
            Indent indent = IIndentedLoggingEvent.class.cast(event).getIndent();
            converted = new StringBuilder()
                    .append(indent.get()).append(converted)
                    .toString();
        }
        
        return converted;
    }

}
