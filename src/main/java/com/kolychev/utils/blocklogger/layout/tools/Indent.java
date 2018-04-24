package com.kolychev.utils.blocklogger.layout.tools;

import com.google.common.base.Strings;

public class Indent {
    
    public  static final String CONTEXT_KEY = "com.kolychev.utils.blocklogger.layout.Indent";
    private static final String TAB_STRING = "    ";

    private final ThreadLocal<Integer>       level         = new InheritableThreadLocal<>();
    private final ThreadLocal<String>        pad           = new InheritableThreadLocal<>();
    
    private void checkInit() {
        if (level.get() == null) {
            level.set(0);
            updatePad();
        }
    }
    
    private void updatePad() {
        pad.set(Strings.repeat(TAB_STRING, level.get()));
    }
    
    public String get() {
        checkInit();
        return pad.get();
    }
    
    public void increment() {
        checkInit();
        int i = level.get();
        level.set(++i);
        updatePad();
    }
    
    public void decrement() {
        checkInit();
        int i = level.get();
        level.set(--i);
        updatePad();
    }
    
}
