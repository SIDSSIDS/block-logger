package com.github.sidssids.blocklogger.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.slf4j.LoggerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.springframework.boot.test.rule.OutputCapture;

public class LogBlockTest {
    
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
            configurator.doConfigure(LogBlockTest.class.getResourceAsStream("/logback.xml"));
        } catch (JoranException je) {
            // StatusPrinter will handle this
            je.printStackTrace(System.err);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
    
    @Test
    public void test_blockStart_withParam() {
        
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block", "param1=%s", "value1");
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "INFO");
        assertEquals(entry.message, "[+] test block (param1=value1)");
        
        log.close();
    }
    
    @Test
    public void test_blockStart_withoutParam() {
                
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "INFO");
        assertEquals(entry.message, "[+] test block");
        
        log.close();
    }
    
    @Test
    public void test_blockStart_level() {
        
        LogBlock log = LogBlockFactory.debug(LogBlockTest.class, "test block");
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "DEBUG");
        assertEquals(entry.message, "[+] test block");
        
        log.close();
    }
    
    @Test
    public void test_blockEnd_empty() {
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        
        log.close();
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 2);
        LogEntry entry = LogEntry.parse(messages.get(1));
        assertEquals(entry.level, "INFO");
        assertNotNull(entry.message);
        assertTrue(entry.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
    }
    
    @Test
    public void test_blockEnd_reportSameLevel() {
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        
        log.reportInfo("report_param=%s", "report_value");
        log.close();
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 2);
        LogEntry entry = LogEntry.parse(messages.get(1));
        assertEquals(entry.level, "INFO");
        assertNotNull(entry.message);
        assertTrue(entry.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): report_param=report_value"));
        
    }
    
    @Test
    public void test_blockEnd_reportDifferentLevel() {
        
        
        LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block");
        log.reportDebug("report_param=%s", "report_value");
        log.close();
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 2);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        
        assertEquals(entry1.level, "INFO");
        assertNotNull(entry1.message);
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertNotNull(entry2.message);
        assertTrue(entry2.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): report_param=report_value"));
        
    }
    
    @Test
    public void test_blockEnd_reportException_withoutResult() {
        
        RuntimeException testEx = new RuntimeException("test exception");
        
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.withException(testEx)
                    .reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 3 + testEx.getStackTrace().length);
        
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_close = LogEntry.parse(messages.get(1));
        
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        
        assertEquals(entry_close.level, "ERROR");
        assertTrue("actual message: " + entry_close.message, entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): Exception: " + testEx.getClass().getName() + "\\[test exception\\]"));
        
        assertEquals(messages.get(2), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(3 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
    }
    
    @Test
    public void test_blockEnd_reportException_withResult() {
        
        RuntimeException testEx = new RuntimeException("test exception");
        
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.withException(testEx)
                    .reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 3 + testEx.getStackTrace().length);
        
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_close = LogEntry.parse(messages.get(1));
        
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        
        assertEquals(entry_close.level, "ERROR");
        assertTrue("actual message: " + entry_close.message, entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\): error_result=error_value; Exception: " + testEx.getClass().getName() + "\\[test exception\\]"));
        
        assertEquals(messages.get(2), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(3 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
    }
    
    @Test
    public void test_tryWithResources() {
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 2);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "[+] test block");
        
        assertEquals(entry2.level, "INFO");
        assertTrue(entry2.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
    }
    
    @Test
    public void test_logMessageWithIndent() {
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.debug("message inside");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_logMessageWithIndentAndParam() {
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            log.debug("message inside param={}", "value");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_logMessageWithIndent_Log4jLogger() {
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            LoggerFactory.getLogger(LogBlockTest.class).debug("message inside with param {}", "value");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_indentGoesBackAfterClose() {
        try (LogBlock log = LogBlockFactory.info(LogBlockTest.class, "test block")) {
            LoggerFactory.getLogger(LogBlockTest.class).debug("message inside with param {}", "value");
            log.reportError();
        }
        LoggerFactory.getLogger(LogBlockTest.class).info("message outside");
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_loggerByString() {
        try (LogBlock log = LogBlockFactory.info("test-logger", "test block")) {
            LoggerFactory.getLogger("test-logger").debug("message inside with param {}", "value");
            log.reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }    
    @Test
    public void test_withoutEncoderConfiguration() {
        
        LoggerFactory.getLogger("logger-with-default-encoder").debug("test message");
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 1);
        
        LogEntry entry = LogEntry.parse(messages.get(0));
        assertEquals(entry.level, "DEBUG");
        assertEquals(entry.message, "msg:test message");
        
    }
    
    @Test
    public void test_messageOutput_withoutEncoderConfiguration_withoutParams() {
        
        try (LogBlock log = LogBlockFactory.info("logger-with-default-encoder", "test block")) {
            LoggerFactory.getLogger("logger-with-default-encoder").debug("message inside with param {}", "value");
            log.reportError();
        }

        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        
        LogEntry entry1 = LogEntry.parse(messages.get(0));
        LogEntry entry2 = LogEntry.parse(messages.get(1));
        LogEntry entry3 = LogEntry.parse(messages.get(2));
        
        assertEquals(entry1.level, "INFO");
        assertEquals(entry1.message, "msg:[+] test block");
        
        assertEquals(entry2.level, "DEBUG");
        assertEquals(entry2.message, "msg:message inside with param value");
        
        assertEquals(entry3.level, "ERROR");
        assertTrue(String.format("Wrong format: %s", entry3.message), entry3.message.matches("msg:\\[-\\] test block \\(PT[\\d\\.]+S\\)"));

    }
    
    @Test
    public void test_withoutProfiling() {
        
        String loggerName = "test-logger-without-profiling";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutProfiling_withParams() {
        
        String loggerName = "test-logger-without-profiling";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutProfiling_withResult() {
        
        String loggerName = "test-logger-without-profiling";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutProfiling_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-profiling";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_withoutProfiling_reportException_withResult() {
        
        String loggerName = "test-logger-without-profiling";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_withoutParams_withoutParams() {
        
        String loggerName = "test-logger-without-params";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertTrue(String.format("Wrong format: %s", entry_close.message), entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
    }
    
    @Test
    public void test_withoutParams_withParamsIgnored() {
        
        String loggerName = "test-logger-without-params";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param=%s", "value")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "INFO");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertTrue(String.format("Wrong format: %s", entry_close.message), entry_close.message.matches("\\[-\\] test block \\(PT[\\d\\.]+S\\)"));
    }
    
    @Test
    public void test_withoutResult() {
        
        String loggerName = "test-logger-without-result";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutResult_withParams() {
        
        String loggerName = "test-logger-without-result";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutResult_withResultIgnored() {
        
        String loggerName = "test-logger-without-result";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutResult_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-result";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_withoutResult_reportException_withResultIgnored() {
        
        String loggerName = "test-logger-without-result";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_withoutExceptionInfo() {
        
        String loggerName = "test-logger-without-exception-info";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutExceptionInfo_withParams() {
        
        String loggerName = "test-logger-without-exception-info";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutExceptionInfo_withResult() {
        
        String loggerName = "test-logger-without-exception-info";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutExceptionInfo_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-exception-info";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_withoutExceptionInfo_reportException_withResult() {
        
        String loggerName = "test-logger-without-exception-info";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_withoutStackTrace() {
        
        String loggerName = "test-logger-without-stack-trace";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutStackTrace_withParams() {
        
        String loggerName = "test-logger-without-stack-trace";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block", "param1=%s", "value1")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutStackTrace_withResult() {
        
        String loggerName = "test-logger-without-stack-trace";
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.reportInfo("result=%s", "result_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
    }
    
    @Test
    public void test_withoutStackTrace_reportException_withoutResult() {
        
        String loggerName = "test-logger-without-stack-trace";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_withoutStackTrace_reportException_withResult() {
        
        String loggerName = "test-logger-without-stack-trace";
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_exceptionFormat() {
        String loggerName = "test-logger-without-stack-trace";
        
        RuntimeException testEx = new RuntimeException("outer", new IllegalArgumentException("inner"));
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx).reportError("error_result=%s", "error_value");
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
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
        
    }
    
    @Test
    public void test_exceptionInfo_GlobalOn_localOff() {
        String loggerName = "test-logger-without-stack-trace";
        
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx, false, null).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        
    }
    
    @Test
    public void test_exceptionInfo_GlobalOff_localOn() {
        String loggerName = "test-logger-without-exception-info";
        
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx, true, null).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: Exception: " + RuntimeException.class.getName() + "[test exception]");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
    }
    
    @Test
    public void test_stackTrace_GlobalOn_localOff() {
        String loggerName = "test-logger-without-exception-info";
        
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx, null, false).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 3);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block");
        
    }

    @Test
    public void test_stackTrace_GlobalOff_localOn() {
        String loggerName = "test-logger-without-stack-trace";
        
        RuntimeException testEx = new RuntimeException("test exception");
        
        
        try (LogBlock log = LogBlockFactory.info(loggerName, "test block")) {
            LoggerFactory.getLogger(loggerName).debug("inside message");
            log.withException(testEx, null, true).reportError();
        }
        
        List<String> messages = Arrays.asList(capture.toString().split("\\n"));
        assertEquals(messages.size(), 4 + testEx.getStackTrace().length);
        LogEntry entry_start = LogEntry.parse(messages.get(0));
        LogEntry entry_msg   = LogEntry.parse(messages.get(1));
        LogEntry entry_close = LogEntry.parse(messages.get(2));
        assertEquals(entry_start.level, "INFO");
        assertEquals(entry_msg.level,   "DEBUG");
        assertEquals(entry_close.level, "ERROR");
        assertEquals(entry_start.message, "[+] test block");
        assertEquals(entry_msg.message,   "    inside message");
        assertEquals(entry_close.message, "[-] test block: Exception: " + RuntimeException.class.getName() + "[test exception]");
        assertEquals(messages.get(3), testEx.toString());
        IntStream.range(0, testEx.getStackTrace().length)
                .forEach(i -> assertEquals(messages.get(4 + i).trim(), "at " + testEx.getStackTrace()[i]));
        
    }
    
}
