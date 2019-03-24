package com.darren.architect_day14.simple2;

/**
 * Created by hcDarren on 2017/10/8.
 * 适配器对象 - 把人民币转成美元
 */

public class Adapter extends RMBAdaptee implements UsdTarget {
    public Adapter(float rmb) {
        super(rmb);
    }

    @Override
    public float getUsd() {
        return getRmb()/5.6f;
    }
}
