package com.github.sidssids.blocklogger.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.github.sidssids.blocklogger.layout.tools.Indent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;

public class LogBlockTest {
    
    private final PrintStream defaultOut = System.out;
    
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
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(this.getClass().getResourceAsStream("/logback.xml"));
        } catch (JoranException je) {
            // StatusPrinter will handle this
            je.printStackTrace(defaultOut);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
    
    @Test
    public void test_blockStart_withParam() {
        ByteArrayOutputStream out = setUpOutStream();
        
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block", "param1=%s", "value1");
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "INFO");
        assertEquals(entry.message, "[+] test block (param1=value1)");
        
        cleanUpStreams(out);
        log.close();
    }
    
    @Test
    public void test_blockStart_withoutParam() {
        ByteArrayOutputStream out = setUpOutStream();
        
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "INFO");
        assertEquals(entry.message, "[+] test block");
        
        cleanUpStreams(out);
        log.close();
    }
    
    @Test
    public void test_blockStart_level() {
        ByteArrayOutputStream out = setUpOutStream();
        
        LogBlock log = LogBlockFactory.debug(LogBlockTest.class, "test block");
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "DEBUG");
        assertEquals(entry.message, "[+] test block");
        
        cleanUpStreams(out);
        log.close();
    }
    
    @Test
    public void test_blockEnd_empty() {
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        log.close();
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "INFO");
        assertNotNull(entry.message);
        assertTrue(entry.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
        cleanUpStreams(out);
    }
    
    @Test
    public void test_blockEnd_reportSameLevel() {
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        log.reportInfo("report_param=%s", "report_value");
        log.close();
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "INFO");
        assertNotNull(entry.message);
        assertTrue(entry.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): report_param=report_value"));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_blockEnd_reportDifferentLevel() {
        
        ByteArrayOutputStream out = setUpOutStream();
        
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        log.reportDebug("report_param=%s", "report_value");
        log.close();
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 2);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        
        assertEquals(entry1.level, "INFO");
        assertNotNull(entry1.message);
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertNotNull(entry2.message);
        assertTrue(entry2.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): report_param=report_value"));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_blockEnd_reportException_withoutResult() {
        
        RuntimeException testEx = new RuntimeException("test exception");
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.withException(testEx)
                    .reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3 + testEx.getStackTrace().length);
        
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_close = LogEntry.parse(messages.get(1));
        
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        
        assertEquals(entry_close.level, "ERROR");
        assertTrue(entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): Exception: " + testEx.getClass().getName() + "\\[test exception\\]"), "actual message: " + entry_close.message);
        
        assertEquals(messages.get(2), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(3 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_blockEnd_reportException_withResult() {
        
        RuntimeException testEx = new RuntimeException("test exception");
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.withException(testEx)
                    .reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3 + testEx.getStackTrace().length);
        
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_close = LogEntry.parse(messages.get(1));
        
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        
        assertEquals(entry_close.level, "ERROR");
        assertTrue(entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): error_result=error_value; Exception: " + testEx.getClass().getName() + "\\[test exception\\]"), "actual message: " + entry_close.message);
        
        assertEquals(messages.get(2), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(3 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_tryWithResources() {
        ByteArrayOutputStream out = setUpOutStream();
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 2);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "INFO");
        assertTrue(entry2.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
        cleanUpStreams(out);
    }
    
    @Test
    public void test_logMessageWithIndent() {
        ByteArrayOutputStream out = setUpOutStream();
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.debug("message inside");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        LogEntry entry3 = LogEntry.parse(messages.get(2));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertEquals(entry2.message, "    message inside");
        
        assertEquals(entry3.level, "ERROR");
        assertTrue(entry3.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
        cleanUpStreams(out);
    }
    
    @Test
    public void test_logMessageWithIndentAndParam() {
        ByteArrayOutputStream out = setUpOutStream();
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.debug("message inside param={}", "value");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        LogEntry entry3 = LogEntry.parse(messages.get(2));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertEquals(entry2.message, "    message inside param=value");
        
        assertEquals(entry3.level, "ERROR");
        assertTrue(entry3.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
        cleanUpStreams(out);
    }
    
    @Test
    public void test_logMessageWithIndent_Log4jLogger() {
        ByteArrayOutputStream out = setUpOutStream();
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            LoggerFactory.getLogger(LogBlockTest.class).debug("message inside with param {}", "value");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        LogEntry entry3 = LogEntry.parse(messages.get(2));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertEquals(entry2.message, "    message inside with param value");
        
        assertEquals(entry3.level, "ERROR");
        assertTrue(entry3.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
        cleanUpStreams(out);
    }
    
    @Test
    public void test_indentGoesBackAfterClose() {
        ByteArrayOutputStream out = setUpOutStream();
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            LoggerFactory.getLogger(LogBlockTest.class).debug("message inside with param {}", "value");
            log.reportError();
        }
        LoggerFactory.getLogger(LogBlockTest.class).info("message outside");
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 4);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        LogEntry entry3 = LogEntry.parse(messages.get(2));
        LogEntry entry4 = LogEntry.parse(messages.get(3));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertEquals(entry2.message, "    message inside with param value");
        
        assertEquals(entry3.level, "ERROR");
        assertTrue(entry3.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
        
        assertEquals(entry4.level, "INFO");
        assertEquals(entry4.message, "message outside");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_loggerByString() {
        ByteArrayOutputStream out = setUpOutStream();
        try (LogBlock log = LogBlockFactory.info("test-logger", "test block")) {
            LoggerFactory.getLogger("test-logger").debug("message inside with param {}", "value");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        LogEntry entry3 = LogEntry.parse(messages.get(2));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertEquals(entry2.message, "    message inside with param value");
        
        assertEquals(entry3.level, "ERROR");
        assertTrue(entry3.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
        cleanUpStreams(out);
    }    
    @Test
    public void test_withoutEncoderConfiguration() {
        ByteArrayOutputStream out = setUpOutStream();
        
        LoggerFactory.getLogger("logger-with-default-encoder").debug("test message");
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "DEBUG");
        assertEquals(entry.message, "msg:test message");
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_messageOutput_withoutEncoderConfiguration_withoutParams() {
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info("logger-with-default-encoder", "test block")) {
            LoggerFactory.getLogger("logger-with-default-encoder").debug("message inside with param {}", "value");
            log.reportError();
        }

        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        LogEntry entry3 = LogEntry.parse(messages.get(2));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "msg:[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertEquals(entry2.message, "msg:message inside with param value");
        
        assertEquals(entry3.level, "ERROR");
        assertTrue(entry3.message.matches("msg:\\[-\\] test block \\(PT[\\d\\.]+S\\)"), String.format("Wrong format: %s", entry3.message));

        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutProfiling() {
        
        String loggerName = "test-logger-without-profiling";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutProfiling_withParams() {
        
        String loggerName = "test-logger-without-profiling";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block (param1=value1)");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutProfiling_withResult() {
        
        String loggerName = "test-logger-without-profiling";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: result=result_value");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutProfiling_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-profiling";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: Exception: " + testEx.getClass().getName() + "[test exception]");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutProfiling_reportException_withResult() {
        
        String loggerName = "test-logger-without-profiling";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: error_result=error_value; Exception: " + testEx.getClass().getName() + "[test exception]");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutParams_withoutParams() {
        
        String loggerName = "test-logger-without-params";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertTrue(entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"), String.format("Wrong format: %s", entry_close.message));
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutParams_withParamsIgnored() {
        
        String loggerName = "test-logger-without-params";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param=%s", "value")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertTrue(entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"), String.format("Wrong format: %s", entry_close.message));
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutResult() {
        
        String loggerName = "test-logger-without-result";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutResult_withParams() {
        
        String loggerName = "test-logger-without-result";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block (param1=value1)");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutResult_withResultIgnored() {
        
        String loggerName = "test-logger-without-result";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutResult_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-result";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: Exception: " + testEx.getClass().getName() + "[test exception]");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutResult_reportException_withResultIgnored() {
        
        String loggerName = "test-logger-without-result";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: Exception: " + testEx.getClass().getName() + "[test exception]");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutExceptionInfo() {
        
        String loggerName = "test-logger-without-exception-info";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutExceptionInfo_withParams() {
        
        String loggerName = "test-logger-without-exception-info";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block (param1=value1)");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutExceptionInfo_withResult() {
        
        String loggerName = "test-logger-without-exception-info";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: result=result_value");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutExceptionInfo_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-exception-info";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutExceptionInfo_reportException_withResult() {
        
        String loggerName = "test-logger-without-exception-info";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: error_result=error_value");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutStackTrace() {
        
        String loggerName = "test-logger-without-stack-trace";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutStackTrace_withParams() {
        
        String loggerName = "test-logger-without-stack-trace";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block (param1=value1)");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutStackTrace_withResult() {
        
        String loggerName = "test-logger-without-stack-trace";
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: result=result_value");
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutStackTrace_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-stack-trace";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: Exception: " + testEx.getClass().getName() + "[test exception]");
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_withoutStackTrace_reportException_withResult() {
        
        String loggerName = "test-logger-without-stack-trace";
        RuntimeException testEx = new RuntimeException("test exception");
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: error_result=error_value; Exception: " + testEx.getClass().getName() + "[test exception]");
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_exceptionFormat() {
        String loggerName = "test-logger-without-stack-trace";
        
        RuntimeException testEx = new RuntimeException("outer", new IllegalArgumentException("inner"));
        
        ByteArrayOutputStream out = setUpOutStream();
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(out.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: error_result=error_value; Exception: " + RuntimeException.class.getName() + "[outer] caused by " + IllegalArgumentException.class.getName() + "[inner]");
        
        cleanUpStreams(out);
    }
    
    @Test
    public void test_indention_property_null() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        String indention = context.getProperty(Indent.Properties.ENABLED_PROPERTY);
        assertNull(indention);
        assertEquals(Indent.getInstance().isEnabled(), Indent.Defaults.ENABLED);
    }
    
    @Test
    public void test_tagString_property_default() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        String tabString = context.getProperty(Indent.Properties.TAB_STRING_PROPERTY);
        assertNull(tabString);
        assertEquals(Indent.getInstance().getTabString(), Indent.Defaults.TAB_STRING);
    }
    
}
