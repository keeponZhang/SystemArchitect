package com.darren.architect_day01.data.entity;

import com.darren.architect_day01.adapter.UserTypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

//使用时不用再使用 GsonBuilder去注册UserTypeAdapter了
@JsonAdapter(UserTypeAdapter.class) //加在类上
public class User2 {
    //省略其它
    @SerializedName(value = "name")
    public String name;
    public int age;
    public String email;

    public User2() {
    }

    public User2(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User2(String name, int age, String email) {
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