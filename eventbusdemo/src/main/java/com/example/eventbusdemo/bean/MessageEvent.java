package com.example.eventbusdemo.bean;

public class MessageEvent extends BaseMessageEvent{

    private String message;

    public MessageEvent(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}