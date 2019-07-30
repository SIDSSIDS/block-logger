package com.github.sidssids.blocklogger.logger.markers;

import java.util.Optional;

public class StartMarker extends BaseMarker {
    
    private Optional<String> params;
    
    public StartMarker(String title) {
        super(StartMarker.class.getSimpleName(), title);
    }

    public Optional<String> getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = Optional.ofNullable(params);
    }

    public StartMarker withParams(final String params) {
        this.params = Optional.ofNullable(params);
        return this;
    }

}
