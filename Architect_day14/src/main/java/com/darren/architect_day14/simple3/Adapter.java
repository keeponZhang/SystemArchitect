package com.darren.architect_day14.simple3;

/**
 * Created by hcDarren on 2017/10/8.
 * 适配器对象 - 把人民币转成美元
 * 对象适配
 */

public class Adapter implements UsdTarget {
    private RMBAdaptee rmbAdaptee;

    public Adapter(RMBAdaptee rmbAdaptee) {
        this.rmbAdaptee = rmbAdaptee;
    }

    @Override
    public float getUsd() {
        return rmbAdaptee.getRmb()/5.6f;
    }
}
