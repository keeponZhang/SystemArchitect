package com.darren.architect_day17.simple1;

import java.util.List;

/**
 * Created by hcDarren on 2017/10/21.
 * 测试客户端
 */

public class Client {
    public static void main(String[] args){
        CarPartBox box = new CarPartBox();
        box.setNumber(500);
        box.setName("尾灯灯罩");
        box.setCarBrand("奥迪");

        // 接下来要去拆分
        List<TruckCar> carList = SplitService.splitBox(box);

        for (TruckCar truckCar : carList) {
            System.out.println("数量："+truckCar.remove().getNumber());
        }
    }
}
