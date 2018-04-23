package com.kolychev.utils.blocklogger.layout;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.kolychev.utils.blocklogger.Main;
import org.slf4j.Marker;

public class BlockloggerPatternLayout extends PatternLayout {

    public BlockloggerPatternLayout() {
        getInstanceConverterMap().put("m", BlockConverter.class.getName());
        getInstanceConverterMap().put("msg", BlockConverter.class.getName());
        getInstanceConverterMap().put("message", BlockConverter.class.getName());
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        Marker m = event.getMarker();
        if (m != null && m instanceof Main.TestMarker) {
            Main.TestMarker mm = Main.TestMarker.class.cast(m);
            getContext().putProperty(BlockConverter.INDENT_KEY, mm.getIndent());
        }
        return super.doLayout(event);
    }

}
