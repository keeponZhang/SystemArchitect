package com.darren.architect_day16.simple1;

/**
 * Created by hcDarren on 2017/10/15.
 */

public class Client {
    public static void main(String[] args){
        Man man = new Man("Darren");
        BankWorker bankWorker = new BankWorker(man);
        bankWorker.applyBank();
    }
}
