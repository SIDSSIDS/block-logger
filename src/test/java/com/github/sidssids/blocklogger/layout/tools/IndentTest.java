package com.github.sidssids.blocklogger.layout.tools;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class IndentTest {

    @Test
    public void test_increment() {
        Indent i = Indent.getInstance().reset().resetEnabled().resetTabString();
        assertEquals(i.get(), "");
        
        i.increment();
        assertEquals(i.get(), "    ");
        
        i.increment();
        assertEquals(i.get(), "        ");
    }

    @Test
    public void test_decrement() {
        Indent i = Indent.getInstance().reset().resetEnabled().resetTabString();
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
        Indent i = Indent.getInstance().reset().resetEnabled().resetTabString();
        
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
        Indent i = Indent.getInstance().reset().resetEnabled().resetTabString();
        
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
