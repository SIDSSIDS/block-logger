package com.github.sidssids.blocklogger.layout.tools;

import org.junit.Test;
import static org.junit.Assert.*;

public class IndentTest {
    
    @Test
    public void test_increment() {
        Indent i = new Indent();
        assertEquals(i.get(), "");
        
        i.increment();
        assertEquals(i.get(), "    ");
        
        i.increment();
        assertEquals(i.get(), "        ");
    }

    @Test
    public void test_decrement() {
        Indent i = new Indent();
        i.increment();
        i.increment();
        assertEquals(i.get(), "        ");
        
        i.decrement();
        assertEquals(i.get(), "    ");
        
        i.decrement();
        assertEquals(i.get(), "");
        
        i.decrement();
        assertEquals(i.get(), "");
    }
    
    @Test
    public void test_tabString() {
        Indent i = new Indent();
        
        i.setTabString("--");
        assertEquals(i.get(), "");
        
        i.increment();
        assertEquals(i.get(), "--");
        
        i.increment();
        assertEquals(i.get(), "----");
        
        i.decrement();
        assertEquals(i.get(), "--");
    }
    
    @Test
    public void test_threadInheritance() throws InterruptedException {
        Indent i = new Indent();
        
        i.increment();
        i.increment();
        assertEquals(i.get(), "        ");
        
        Thread t = new Thread(() -> {
            i.increment();
            assertEquals(i.get(), "            ");
            
            i.decrement();
            assertEquals(i.get(), "        ");
        });
        
        t.start();
        t.join();
        
        assertEquals(i.get(), "        ");
        
        i.decrement();
        assertEquals(i.get(), "    ");
    }
    
}
