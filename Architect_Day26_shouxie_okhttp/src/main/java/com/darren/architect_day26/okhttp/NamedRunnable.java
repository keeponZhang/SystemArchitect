package com.darren.architect_day26.okhttp;

/**
 * Created by hcDarren on 2017/11/18.
 */

public abstract class NamedRunnable implements Runnable{
    @Override
    public void run() {
        execute();
    }

    protected abstract void execute();
}
