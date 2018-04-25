package com.github.sidssids.blocklogger.encoder;

import com.github.sidssids.blocklogger.layout.BaseBlockloggerPatternLayout;
import com.github.sidssids.blocklogger.layout.CollapsibleBlockloggerPatternLayout;

public class CollapsibleBlockloggerPatternLayoutEncoder extends BaseBlockloggerPatternLayoutEncoder {

    @Override
    protected BaseBlockloggerPatternLayout createPatternLayout() {
        return new CollapsibleBlockloggerPatternLayout(indent);
    }
    
}
