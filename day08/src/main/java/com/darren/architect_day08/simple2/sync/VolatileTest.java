package com.darren.architect_day08.simple2.sync;

/**
 * Created by hcDarren on 2017/9/17.
 */

public class VolatileTest {
    public static void main(String[] args) {
        ThreadDemo td = new ThreadDemo();
        new Thread(td).start();
        while(true){
            if(td.isFlag()){
                System.out.println("------------------");
                break;
            }
        }

        // 执行结果？ flag= true  ------------------
    }
}
class ThreadDemo implements Runnable {
    private volatile boolean flag = false;
    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        flag = true;
        System.out.println("flag=" + isFlag());
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
