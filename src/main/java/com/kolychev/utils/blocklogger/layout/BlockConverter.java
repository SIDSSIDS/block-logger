package com.kolychev.utils.blocklogger.layout;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Optional;

public class BlockConverter extends MessageConverter {
    
    public  static final String INDENT_KEY   = "com.kolychev.utils.blocklogger.layout.BlockConverter.INDENT_KEY";
    private static final String EMPTY_STRING = "";

    public BlockConverter() {
    }

    @Override
    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        String indent  = Optional.ofNullable(getContext().getProperty(INDENT_KEY)).orElse(EMPTY_STRING);
        return String.format("%s%s", indent, message);
    }

}
