package com.kolychev.utils.blocklogger.encoder;

import com.kolychev.utils.blocklogger.layout.BaseBlockloggerPatternLayout;
import com.kolychev.utils.blocklogger.layout.CollapsibleBlockloggerPatternLayout;

public class CollapsibleBlockloggerPatternLayoutEncoder extends BaseBlockloggerPatternLayoutEncoder {

    @Override
    protected BaseBlockloggerPatternLayout createPatternLayout() {
        return new CollapsibleBlockloggerPatternLayout(indent);
    }
    
}
