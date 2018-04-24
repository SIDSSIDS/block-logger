package com.kolychev.utils.blocklogger.layout;

import com.kolychev.utils.blocklogger.layout.tools.Indent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.kolychev.utils.blocklogger.logger.markers.CloseMarker;

public class CollapsibleBlockloggerPatternLayout extends BaseBlockloggerPatternLayout {

    private final BlockOpeningEvent blockOpen = new BlockOpeningEvent();

    public CollapsibleBlockloggerPatternLayout(Indent indent) {
        super(indent);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        boolean closing = isClosing(event);
        boolean opening = isOpening(event);
        boolean common  = !closing && !opening;
        
        String openBlockMessage  = getOpeningMessage(event);
        String closeBlockMessage = closing ? getClosingMessage(event, openBlockMessage != null) : null;
        String commonMessage     = common ? super.doLayout(event) : null;
        
        if (openBlockMessage != null && closing && isSkipped(event)) {
            openBlockMessage = null;
        }
        
        if (opening) {
            blockOpen.pushEvent(event);
        }
        
        if (openBlockMessage == null
                && commonMessage == null
                && closeBlockMessage == null) {
            return null;
        }
        
        StringBuilder finalMessage = new StringBuilder();
        if (openBlockMessage != null) {
            finalMessage.append(openBlockMessage);
        }
        if (commonMessage != null) {
            finalMessage.append(commonMessage);
        }
        if (closeBlockMessage != null) {
            finalMessage.append(closeBlockMessage);
        }
        return finalMessage.toString();
    }
    
    private boolean isSkipped(ILoggingEvent event) {
        return CloseMarker.class.cast(event.getMarker()).isSkipped();
    }
    
    private String getOpeningMessage(ILoggingEvent nextEvent) {
        ILoggingEvent openBlockEvent = blockOpen.popEvent();
        if (openBlockEvent != null) {
            try {
                openBlockEvent = generateOpenBlockEvent(openBlockEvent, nextEvent);
                return super.doLayout(openBlockEvent);
            } finally {
                indent.increment();
            }
        } else {
            return null;
        }
    }
    
    private String getClosingMessage(ILoggingEvent event, boolean collapsed) {
        indent.decrement();
        if (collapsed) {
            return null;
        }
        event = generateCloseBlockEvent(event);
        return super.doLayout(event);
    }
    
    private class BlockOpeningEvent {
        
        private final ThreadLocal<ILoggingEvent> openEvent = new InheritableThreadLocal<>();
        
        public void pushEvent(ILoggingEvent event) {
            openEvent.set(event);
        }

        public ILoggingEvent popEvent() {
            ILoggingEvent event = openEvent.get();
            openEvent.remove();
            return event;
        }

    }
    
}
