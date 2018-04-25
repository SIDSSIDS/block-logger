package com.kolychev.utils.blocklogger.encoder;

import com.kolychev.utils.blocklogger.layout.BaseBlockloggerPatternLayout;
import com.kolychev.utils.blocklogger.layout.BlockloggerPatternLayout;

public class BlockloggerPatternLayoutEncoder extends BaseBlockloggerPatternLayoutEncoder {

    @Override
    protected BaseBlockloggerPatternLayout createPatternLayout() {
        return new BlockloggerPatternLayout(indent);
    }
    
}
