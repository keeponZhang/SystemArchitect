package com.darren.architect_day08.simple2.sync;

/**
 * 单例设计模式 - 懒汉式
 * Created by hcDarren on 2017/9/17.
 */
public class Singleton2 {
    // 只有使用的时候才会去 new 对象 ，可能更加高效
    // 但是会有一些问题？多线程并发的问题,如果多线程调用还是会存在多个实例
    private static Singleton2 mInstance;

    private Singleton2() {

    }

    // 虽说解决了线程安全的问题，但是又会出现效率的问题，
    // 即保证线程的安全同是效率也是比较高的
    // 这种方式其实还是会有问题？
    public static Singleton2 getInstance() {
        if (mInstance == null) {
            synchronized (Singleton2.class) {
                if (mInstance == null) {
                    mInstance = new Singleton2();
                }
            }
        }
        return mInstance;
    }
}
