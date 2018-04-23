package com.kolychev.utils.blocklogger.layout;

import com.google.common.base.Strings;

public class Indent {
    
    public  static final String CONTEXT_KEY = "com.kolychev.utils.blocklogger.layout.Indent";
    private static final String TAB_STRING = "  ";

    private final ThreadLocal<Integer> level = new InheritableThreadLocal<>();
    private final ThreadLocal<String>  pad   = new InheritableThreadLocal<>();
    
    public Indent() {
        level.set(0);
        updatePad();
    }
    
    private void updatePad() {
        pad.set(Strings.repeat(TAB_STRING, level.get()));
    }
    
    public String get() {
        return pad.get();
    }
    
    public void increment() {
        int i = level.get();
        level.set(++i);
        updatePad();
    }
    
    public void decrement() {
        int i = level.get();
        level.set(--i);
        updatePad();
    }
    
}
