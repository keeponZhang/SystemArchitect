package com.darren.architect_day17.simple2;

/**
 * Created by hcDarren on 2017/10/21.
 * 具体的出货的物品 - 汽车的零件
 */

public class CarPartBox implements IBox {
    private int number;
    private String name;
    private String carBrand;// 汽车的品牌
    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public IBox copy() {
        CarPartBox box = new CarPartBox();
        box.setName(name);
        box.setNumber(number);
        box.setCarBrand(carBrand);
        return box;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }
}
