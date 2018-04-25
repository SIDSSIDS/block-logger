package com.github.sidssids.blocklogger.logger.markers;

import java.util.Optional;

public class StartMarker extends BaseMarker {
    
    private final Optional<String> params;

    public StartMarker(String title, String params) {
        super(StartMarker.class.getSimpleName(), title);
        this.params = Optional.ofNullable(params);
    }

    public Optional<String> getParams() {
        return params;
    }

}
