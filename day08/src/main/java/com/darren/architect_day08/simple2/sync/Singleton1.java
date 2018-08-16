package com.darren.architect_day08.simple2.sync;

/**
 * 单例设计模式 - 懒汉式
 * Created by hcDarren on 2017/9/17.
 */
public class Singleton1 {
    // 只有使用的时候才会去 new 对象 ，可能更加高效
    // 但是会有一些问题？多线程并发的问题,如果多线程调用还是会存在多个实例
    private static Singleton1 mInstance;

    private Singleton1(){

    }
    // 虽说解决了线程安全的问题，但是又会出现效率的问题，
    // 又会显得比较低，每次获取都要经过同步锁的判断
    public static synchronized Singleton1 getInstance(){
        if(mInstance == null){
            mInstance = new Singleton1();
        }
        return mInstance;
    }
}
