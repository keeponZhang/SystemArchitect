package com.darren.architect_day20.simple1;

import java.util.Random;

/**
 * Created by hcDarren on 2017/10/29.
 * 火车票
 */
public class Ticket {
    String from;
    String to;

    public Ticket(String form, String to) {
        this.from = form;
        this.to = to;
    }

    int getPrice(){
        return new Random().nextInt(100)+20;
    }
}
