package com.github.sidssids.blocklogger.layout.tools;

import java.util.stream.IntStream;

public class Indent {
    
    public  static final String CONTEXT_KEY = "com.kolychev.utils.blocklogger.layout.Indent";
    private static final String DEFAULT_TAB_STRING = "    ";
    
    private final ThreadLocal<Integer> level = new InheritableThreadLocal<>();
    private final ThreadLocal<String>  pad   = new InheritableThreadLocal<>();
    private       String               tabString;

    public Indent() {
        tabString = DEFAULT_TAB_STRING;
    }

    public String getTabString() {
        return tabString;
    }

    public void setTabString(String tabString) {
        this.tabString = tabString;
    }

    private void checkInit() {
        if (level.get() == null) {
            level.set(0);
            updatePad();
        }
    }
    
    private String repeat(String str, int times) {
        StringBuilder builder = new StringBuilder(str.length() * times);
        IntStream.range(0, times).forEach(i -> builder.append(str));
        return builder.toString();
    }
    
    private void updatePad() {
        pad.set(repeat(tabString, level.get()));
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