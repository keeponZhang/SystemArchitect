package com.darren.architect_day08.simple2;

/**
 * 单例设计模式 - 懒汉式
 * Created by hcDarren on 2017/9/17.
 */
public class Singleton {
    // 只有使用的时候才会去 new 对象 ，可能更加高效
    // 但是会有一些问题？多线程并发的问题,如果多线程调用还是会存在多个实例
    private static Singleton mInstance;

    private Singleton(){

    }

    public static Singleton getInstance(){
        if(mInstance == null){
            mInstance = new Singleton();
        }
        return mInstance;
    }
}
