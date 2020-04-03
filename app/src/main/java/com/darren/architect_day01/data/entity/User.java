package com.darren.architect_day01.data.entity;

import com.google.gson.annotations.SerializedName;

public class User {
    //省略其它 测试
    @SerializedName(value = "name")
    public String name;
    public int age;
    public String email;

    public User() {
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}