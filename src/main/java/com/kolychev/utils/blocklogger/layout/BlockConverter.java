package com.kolychev.utils.blocklogger.layout;

import com.kolychev.utils.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class BlockConverter extends MessageConverter {
    
    private Indent indent;

    @Override
    public void start() {
        super.start();
        indent = (Indent)getContext().getObject(Indent.CONTEXT_KEY);
    }
    
    @Override
    public String convert(ILoggingEvent event) {
        return String.format("%s%s", indent.get(), super.convert(event));
    }

}
