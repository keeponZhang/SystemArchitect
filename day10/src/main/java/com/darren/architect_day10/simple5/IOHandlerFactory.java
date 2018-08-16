package com.darren.architect_day10.simple5;

/**
 * 工厂设计模式 - 简单工厂模式
 * Created by hcDarren on 2017/9/24.
 */
public class IOHandlerFactory {
    // 如果觉得有必要那么完全可以写成单例设计模式

    // 问题，比如我新增一个新的方式存储，要怎么改？
    // 需要新增类型需要添加 case 说白了就是需要改动原来的很多代码
    public static IOHandler createIOHandler(Class<? extends IOHandler> ioHandlerClass){
        try {
            return ioHandlerClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PreferencesIOHandler();
    }

    /**
     * 获取 运行内存 存储
     * @return
     */
    public static IOHandler getMemoryIOHandler(){
        return createIOHandler(MemoryIOHandler.class);
    }

    /**
     * 获取 磁盘 存储
     * @return
     */
    public static IOHandler getDiskIOHandler(){
        return createIOHandler(DiskIOHandler.class);
    }

    /**
     * 获取 SP 存储
     * @return
     */
    public static IOHandler getPreferencesIOHandler(){
        return createIOHandler(PreferencesIOHandler.class);
    }

    /**
     * 获取 默认 存储
     * 为什么搞个默认的，有时候代码写完了，但是网上有很多高效的，
     * 又或者是本来就是用了别人的，但是某些人出了更好的，这样方便切换
     * @return
     */
    public static IOHandler getDefaultIOHandler(){
        return getPreferencesIOHandler();
    }
}
