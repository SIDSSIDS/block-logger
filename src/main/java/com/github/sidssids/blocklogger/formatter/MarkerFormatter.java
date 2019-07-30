package com.github.sidssids.blocklogger.formatter;

import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import java.time.Duration;
import java.util.Optional;

public class MarkerFormatter {
    
    public static String generateOpenBlockMessage(StartMarker marker) {
        return generateOpenBlockMessage(marker.getTitle(), marker.getParams());
    }
    
    public static String generateOpenBlockMessage(String title, Optional<String> params) {
        StringBuilder message = new StringBuilder();
        
        message.append("[+] ");
        message.append(title);
        if (params.isPresent()) {
            message.append(" (").append(params.get()).append(")");
        }
        
        return message.toString();
    }
    
    public static String generateCloseBlockMessage(boolean profiling, CloseMarker marker) {
        return generateCloseBlockMessage(marker.getTitle(), marker.getDuration(), profiling, marker.getResult(), marker.getException());
    }
    
    public static String generateCloseBlockMessage(String title, Duration duration, boolean profiling, Optional<String> result, Optional<Throwable> exception) {
        StringBuilder message = new StringBuilder();
        
        message.append("[-] ").append(title);
        if (profiling) {
            message.append(" (").append(duration.toString()).append(")");
        }
        return generateCloseBlockResult(message, result, exception)
                .toString();
    }
    
    private static StringBuilder generateCloseBlockResult(StringBuilder message, Optional<String> result, Optional<Throwable> exception) {
        if (result.isPresent() || exception.isPresent()) {
            message.append(": ");
        }
        if (result.isPresent()) {
            message.append(result.get());
        }
        if (exception.isPresent()) {
            if (message.length() > 0) {
                message.append("; ");
            }
            message.append("Exception: ").append(exception.get().toString());
        }
        return message;
    }    
}
