package com.github.sidssids.blocklogger.logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;

class LogEntry {

    private static final Pattern PATTERN = Pattern.compile("(?<timestamp>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}) " + "\\[(?<threadName>[^\\]]+)\\] " + "(?<level>.{5}) " + "(?<message>.*)");
    String source;
    String timestamp;
    String level;
    String threadName;
    String message;

    public LogEntry withSource(final String source) {
        this.source = source;
        return this;
    }

    public LogEntry withTimestamp(final String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public LogEntry withLevel(final String level) {
        this.level = level;
        return this;
    }

    public LogEntry withThreadName(final String threadName) {
        this.threadName = threadName;
        return this;
    }

    public LogEntry withMessage(final String message) {
        this.message = message;
        return this;
    }

    static LogEntry parse(String message) {
        Matcher m = PATTERN.matcher(message);
        if (m.find()) {
            return new LogEntry().withSource(message).withTimestamp(m.group("timestamp").trim()).withThreadName(m.group("threadName").trim()).withLevel(m.group("level").trim()).withMessage(m.group("message"));
        } else {
            Assert.fail(String.format("'%s' doesn't match pattern: %s", message, PATTERN.pattern()));
            return null;
        }
    }

}
