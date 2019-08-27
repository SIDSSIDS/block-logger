package com.github.sidssids.blocklogger.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class LogBlockTabStringTest {
    
    private static final PrintStream defaultOut = System.out;
    
    private ByteArrayOutputStream setUpOutStream() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        return out;
    }
    
    private void cleanUpStreams(ByteArrayOutputStream out) {
        try {
            System.setOut(defaultOut);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @BeforeClass
    public static void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(LogBlockTabStringTest.class.getResourceAsStream("/logback_tab_string.xml"));
        } catch (JoranException je) {
            // StatusPrinter will handle this
            je.printStackTrace(defaultOut);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
    
    @Test
    public void test_tabString() {
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(this.getClass(), "test block")) {
            try (LogBlock log2 = LogBlockFactory.info(this.getClass(), "inner test block")) {
                LoggerFactory.getLogger(this.getClass()).debug("inside message 2");
            }
            LoggerFactory.getLogger(this.getClass()).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 6);
        LogEntry entry_start       = LogEntry.parse(messages.get(0));
        LogEntry entry_start_inner = LogEntry.parse(messages.get(1));
        LogEntry entry_msg         = LogEntry.parse(messages.get(2));
        LogEntry entry_close_inner = LogEntry.parse(messages.get(3));
        LogEntry entry_msg2        = LogEntry.parse(messages.get(4));
        LogEntry entry_close       = LogEntry.parse(messages.get(5));
        
        assertEquals(entry_start.level,       "INFO");
        assertEquals(entry_start_inner.level, "INFO");
        assertEquals(entry_msg.level,         "DEBUG");
        assertEquals(entry_msg2.level,        "DEBUG");
        assertEquals(entry_close_inner.level, "INFO");
        assertEquals(entry_close.level,       "INFO");
        
        assertEquals(entry_start.message,       "[+] test block");
        assertEquals(entry_start_inner.message, "|---[+] inner test block");
        assertEquals(entry_msg.message,         "|---|---inside message 2");
        assertEquals(entry_close_inner.message, "|---[-] inner test block");
        assertEquals(entry_msg2.message,        "|---inside message");
        assertEquals(entry_close.message,       "[-] test block");
        cleanUpStreams(out);
    }

}
