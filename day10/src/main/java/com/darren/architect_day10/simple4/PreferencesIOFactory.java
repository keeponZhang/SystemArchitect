package com.darren.architect_day10.simple4;

/**
 * Created by hcDarren on 2017/9/24.
 */

public class PreferencesIOFactory implements IOFactory{
    @Override
    public IOHandler createIOHandler() {
        return new PreferencesIOHandler();
    }
}
