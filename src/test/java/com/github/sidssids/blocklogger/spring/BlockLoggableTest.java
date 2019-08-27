package com.github.sidssids.blocklogger.spring;

import com.github.sidssids.blocklogger.logger.LogEntry;
import com.github.sidssids.blocklogger.spring.annotation.BlockLoggable;
import com.github.sidssids.blocklogger.spring.config.EnableLogBlock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlockLoggableTest {
    
    @Rule
    public OutputCapture capture = new OutputCapture();

    @Autowired
    private TestService service;
    
    @Test
    public void test() {
        service.testMethod();
        String[] lines = capture.toString().split(System.lineSeparator());
        assertEquals(3, lines.length);
        LogEntry open  = LogEntry.parse(lines[0]);
        LogEntry msg   = LogEntry.parse(lines[1]);
        LogEntry close = LogEntry.parse(lines[2]);
        
        assertEquals("INFO",  open.level);
        assertEquals("DEBUG", msg.level);
        assertEquals("INFO",  close.level);
        
        assertEquals("[+] testMethod", open.message);
        assertEquals("    test message", msg.message);
        assertTrue(close.message.matches("\\[-\\] testMethod \\(PT[\\d\\.]+S\\)"));
    }
    
    public static class TestService {
        
        @BlockLoggable(loggerName = "test-logger", appendResult = false)
        public void testMethod() {
            LoggerFactory.getLogger("test-logger").debug("test message");
        }
        
    }
    
    @Configuration
    @EnableAspectJAutoProxy
    @EnableLogBlock
    public static class Application {
        
        @Bean
        public TestService testService() {
            return new TestService();
        }
        
    }
}
