package com.darren.architect_day10.simple4;

/**
 * 运行内存存储的 Factory
 * Created by hcDarren on 2017/9/24.
 */

public class MemoryIOFactory implements IOFactory{
    @Override
    public IOHandler createIOHandler() {
        return new MemoryIOHandler();
    }
}
