package com.darren.architect_day21.simple1;

/**
 * Created by hcDarren on 2017/11/4.
 */

public class Client {
    public static void main(String[] args){
        Order order = new Order();
        order.pay();
        order.deliverGoods();
    }
}
