package com.github.sidssids.blocklogger.layout.tools;

import java.util.stream.IntStream;

public class Indent {
    
    public static class Defaults {
        public static final boolean ENABLED    = true;
        public static final String  TAB_STRING = "    ";
    }
    
    public static class Properties {
        public static final String ENABLED_PROPERTY    = "com.github.sidssids.blocklogger.indention.enabled";
        public static final String TAB_STRING_PROPERTY = "com.github.sidssids.blocklogger.indention.tabString";
    }
    
    private static final Indent instance = new Indent();
    
    private final ThreadLocal<Integer> level = new InheritableThreadLocal<>();
    private final ThreadLocal<String>  pad   = new InheritableThreadLocal<>();
    
    private String  tabString;
    private boolean enabled;
    
    public static Indent getInstance() {
        return instance;
    }

    private Indent() {
        tabString = Defaults.TAB_STRING;
        enabled = Defaults.ENABLED;
    }

    public String getTabString() {
        return tabString;
    }
    
    public Indent resetTabString() {
        tabString = Defaults.TAB_STRING;
        return this;
    }

    public void setTabString(String tabString) {
        this.tabString = isNull(tabString, Defaults.TAB_STRING);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = isNull(enabled, Defaults.ENABLED);
    }
    
    public void setEnabled(String enabledStr) {
        this.enabled = Boolean.parseBoolean(isNull(enabledStr, String.valueOf(Defaults.ENABLED)));
    }
    
    public Indent resetEnabled() {
        enabled = Defaults.ENABLED;
        return this;
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
    
    public Indent reset() {
        level.set(0);
        updatePad();
        return this;
    }
    
    public String get() {
        if (isEnabled()) {
            checkInit();
            return pad.get();
        } else {
            return "";
        }
    }
    
    public void increment() {
        if (isEnabled()) {
            checkInit();
            int i = level.get();
            level.set(++i);
            updatePad();
        }
    }
    
    public void decrement() {
        if (isEnabled()) {
            checkInit();
            int i = level.get();
            if (i > 0) {
                level.set(--i);
                updatePad();
            }
        }
    }
    
    private <T> T isNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
