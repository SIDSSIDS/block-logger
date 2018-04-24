package com.kolychev.utils.blocklogger.encoder;

import ch.qos.logback.classic.PatternLayout;
import com.kolychev.utils.blocklogger.layout.BlockloggerPatternLayout;

public class BlockloggerPatternLayoutEncoder extends BaseBlockloggerPatternLayoutEncoder {

    @Override
    protected PatternLayout createPatternLayout() {
        return new BlockloggerPatternLayout(indent);
    }
    
}
