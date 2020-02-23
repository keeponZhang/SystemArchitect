package com.darren.architect_day01.data.entity;

public class Result<T> {
    public int code;
    public String message;
    public T data;

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}