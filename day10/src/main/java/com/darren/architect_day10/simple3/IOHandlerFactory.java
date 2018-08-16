package com.darren.architect_day10.simple3;

/**
 * 工厂设计模式 - 简单工厂模式
 * Created by hcDarren on 2017/9/24.
 */
public class IOHandlerFactory {
    public enum IOType{
        MEMORY,PREFERENCES,DISK
    }
    // 问题，比如我新增一个新的方式存储，要怎么改？
    // 需要新增类型需要添加 case 说白了就是需要改动原来的很多代码
    public static IOHandler createIOHandler(IOType ioType){
        switch (ioType){
            case MEMORY:
                // 直接返回一个对象，有的时候我们需要创建对象之后，要进行一系列的初始化参数
                return new MemoryIOHandler();
            case PREFERENCES:
                return new PreferencesIOHandler();
            case DISK:
                return new DiskIOHandler();
            default:
                return null;
        }
    }
}
