package com.kolychev.utils.blocklogger.encoder;

import ch.qos.logback.classic.PatternLayout;
import com.kolychev.utils.blocklogger.layout.CollapsibleBlockloggerPatternLayout;

public class CollapsibleBlockloggerPatternLayoutEncoder extends BaseBlockloggerPatternLayoutEncoder {

    @Override
    protected PatternLayout createPatternLayout() {
        return new CollapsibleBlockloggerPatternLayout(indent);
    }
    
}
