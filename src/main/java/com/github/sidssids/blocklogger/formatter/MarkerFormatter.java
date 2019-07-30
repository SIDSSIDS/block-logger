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
        
        boolean appendResult = settings.isAppendResult()        && marker.getResult().isPresent();
        boolean appendExInfo = settings.isAppendExceptionInfo() && marker.getException().isPresent();
        
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
            message.append("Exception: ").append(marker.getException().get().toString());
        }
        return message.toString();
    }
    
    private static Settings getOrDefault(Settings settings) {
        return settings != null ? settings : Settings.Defaults.SETTINGS;
    }
    
}
