package com.darren.architect_day17.simple4;

/**
 * Created by hcDarren on 2017/10/21.
 * 用户的对象
 */
public class User implements Cloneable{
    public String userName;
    public int age;
    public Address userAddress;

    @Override
    protected User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }

    public static class Address{
        public Address(String addressName, String city) {
            this.addressName = addressName;
            this.city = city;
        }

        public String addressName;
        public String city;
    }
}
