package com.github.sidssids.blocklogger.encoder;

import com.github.sidssids.blocklogger.layout.BaseBlockloggerPatternLayout;
import com.github.sidssids.blocklogger.layout.BlockloggerPatternLayout;

public class BlockloggerPatternLayoutEncoder extends BaseBlockloggerPatternLayoutEncoder {

    @Override
    protected BaseBlockloggerPatternLayout createPatternLayout() {
        return new BlockloggerPatternLayout(indent);
    }
    
}
