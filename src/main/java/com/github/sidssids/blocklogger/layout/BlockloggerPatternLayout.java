package com.github.sidssids.blocklogger.layout;

import com.github.sidssids.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class BlockloggerPatternLayout extends BaseBlockloggerPatternLayout {

    public BlockloggerPatternLayout(Indent indent) {
        super(indent);
    }
    
    @Override
    public String doLayout(ILoggingEvent event) {
        boolean closing = isClosing(event);
        boolean opening = isOpening(event);
        
        if (opening) {
            event = generateOpenBlockEvent(event, null);
        }
        if (closing) {
            event = generateCloseBlockEvent(event);
        }
        
        if (closing) {
            indent.decrement();
        }
        String result = super.doLayout(event);
        if (opening) {
            indent.increment();
        }
        return result;
    }
    

}
