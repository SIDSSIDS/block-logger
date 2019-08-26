package com.github.sidssids.blocklogger.spring;

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
        service.test();
        String[] lines = capture.toString().split(System.lineSeparator());
        assertEquals(3, lines.length);
    }
    
    public static class TestService {
        
        @BlockLoggable(loggerName = "test-logger")
        public void test() {
            LoggerFactory.getLogger("test-logger").info("test");
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
