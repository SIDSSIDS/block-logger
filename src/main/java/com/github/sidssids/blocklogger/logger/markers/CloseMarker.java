package com.github.sidssids.blocklogger.logger.markers;

import java.time.Duration;
import java.util.Optional;

public class CloseMarker extends BaseMarker {
    
    private final Duration            duration;
    private final boolean             skip;
    private final Optional<String>    result;
    private final Optional<Throwable> exception;

    public CloseMarker(String title, Duration duration, String result, Throwable exception, boolean skip) {
        super(CloseMarker.class.getSimpleName(), title);
        this.duration = duration;
        this.result = Optional.ofNullable(result);
        this.exception = Optional.ofNullable(exception);
        this.skip = skip;
    }
    
    public Duration getDuration() {
        return duration;
    }

    public Optional<String> getResult() {
        return result;
    }

    public Optional<Throwable> getException() {
        return exception;
    }

    public boolean isSkipped() {
        return skip;
    }

}
