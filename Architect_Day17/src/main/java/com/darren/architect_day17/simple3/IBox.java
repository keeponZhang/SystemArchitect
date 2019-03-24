package com.darren.architect_day17.simple3;

/**
 * Created by hcDarren on 2017/10/21.
 * 出货的箱子接口
 */

public abstract class IBox implements Cloneable{
    abstract void setNumber(int number);// 设置箱子的数量

    abstract int getNumber();// 有多少货

    @Override
    protected IBox clone() throws CloneNotSupportedException {
        return (IBox) super.clone();
    }
}
