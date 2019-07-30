package com.github.sidssids.blocklogger.layout;

import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Optional;

public class IndentedMessageConverter extends MessageConverter {
    
    private boolean indention = true;

    @Override
    public void start() {
        indention = Optional
                    .ofNullable(getContext().getProperty("com.github.sidssids.blocklogger.indention"))
                    .map(Boolean::new)
                    .orElse(true);
        super.start();
    }
    
    @Override
    public String convert(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        if (indention) {
            sb.append(Indent.getInstance().get());
        }
        return sb.append(super.convert(event)).toString();
    }

}
