package com.github.sidssids.blocklogger.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.rule.OutputCapture;

public class LogBlockWithTwoAppendersTest {
    
    @Rule
    public OutputCapture capture = new OutputCapture();
    
    @BeforeClass
    public static void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(LogBlockWithoutIndentionTest.class.getResourceAsStream("/logback_with_two_appenders.xml"));
        } catch (JoranException je) {
            // StatusPrinter will handle this
            je.printStackTrace(System.out);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
    
    @Test
    public void test_with_two_appenders() {
        
        try (LogBlock log = LogBlockFactory.info(this.getClass(), "test block")) {
            LoggerFactory.getLogger(this.getClass()).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(6, messages.size());
        LogEntry entry_start  = LogEntry.parse(messages.get(0));
        LogEntry entry_start2 = LogEntry.parse(messages.get(1));
        LogEntry entry_msg    = LogEntry.parse(messages.get(2));
        LogEntry entry_msg2   = LogEntry.parse(messages.get(3));
        LogEntry entry_close  = LogEntry.parse(messages.get(4));
        LogEntry entry_close2 = LogEntry.parse(messages.get(5));
        
        assertEquals("INFO",  entry_start.level);
        assertEquals("INFO",  entry_start2.level);
        assertEquals("DEBUG", entry_msg.level);
        assertEquals("DEBUG", entry_msg2.level);
        assertEquals("INFO",  entry_close2.level);
        assertEquals("INFO",  entry_close.level);
        
        assertEquals("[+] test block",     entry_start.message);
        assertEquals("[+] test block",     entry_start2.message);
        assertEquals("    inside message", entry_msg.message);
        assertEquals("    inside message", entry_msg2.message);
        assertEquals("[-] test block",     entry_close.message);
        assertEquals("[-] test block",     entry_close2.message);
    }

}
