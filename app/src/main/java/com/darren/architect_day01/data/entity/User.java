package com.darren.architect_day01.data.entity;

import com.google.gson.annotations.SerializedName;

public class User {
    //省略其它
    @SerializedName(value = "name1")
    public String name;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}