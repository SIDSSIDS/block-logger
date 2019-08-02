package com.github.sidssids.blocklogger.formatter;

import com.github.sidssids.blocklogger.config.Settings;
import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;

public class MarkerFormatter {
    
    public static String generateOpenBlockMessage(StartMarker marker) {
        return generateOpenBlockMessage(null, marker);
    }
    
    public static String generateOpenBlockMessage(Settings settings, StartMarker marker) {
        
        settings = getOrDefault(settings);
        
        StringBuilder message = new StringBuilder();
        
        message.append("[+] ");
        message.append(marker.getTitle());
        if (settings.isAppendParams() && marker.getParams().isPresent()) {
            message.append(" (").append(marker.getParams().get()).append(")");
        }
        
        return message.toString();
    }
    
    public static String generateCloseBlockMessage(CloseMarker marker) {
        return generateCloseBlockMessage(null, marker);
    }
    
    public static String generateCloseBlockMessage(Settings settings, CloseMarker marker) {
        
        settings = getOrDefault(settings);
        
        StringBuilder message = new StringBuilder();
        
        // profiling
        message.append("[-] ").append(marker.getTitle());
        if (settings.isProfiling()) {
            message.append(" (").append(marker.getDuration().toString()).append(")");
        }
        
        boolean appendResult = marker.getResult().isPresent()    && settings.isAppendResult();
        boolean appendExInfo = marker.getException().isPresent() && marker.getAppendExceptionInfo().orElse(settings.isAppendExceptionInfo());
        
        if (appendResult || appendExInfo) {
            message.append(": ");
        }
        
        // result
        if (appendResult) {
            message.append(marker.getResult().get());
        }
        
        // exception
        if (appendExInfo) {
            if (appendResult) {
                message.append("; ");
            }
            message.append("Exception: ");
            appendExceptionInfo(message, marker.getException().get());
        }
        return message.toString();
    }
    
    private static void appendExceptionInfo(StringBuilder message, Throwable e) {
        appendThrowable(message, e);
        
        Throwable cause = e.getCause();
        while (cause != null) {
            message.append(" caused by ");
            appendThrowable(message, cause);
            cause = cause.getCause();
        }
        
    }
    
    private static void appendThrowable(StringBuilder message, Throwable e) {
        message.append(e.getClass().getName());
        if (e.getMessage() != null) {
            message.append("[").append(e.getMessage()).append("]");
        }
    }
    
    private static Settings getOrDefault(Settings settings) {
        return settings != null ? settings : Settings.Defaults.SETTINGS;
    }
    
}
