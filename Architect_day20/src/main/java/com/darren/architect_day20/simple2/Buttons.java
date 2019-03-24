package com.darren.architect_day20.simple2;

/**
 * Created by hcDarren on 2017/10/29.
 */

public class Buttons {
    LeftCommand leftCommand;

    RightCommand rightCommand;


    public void setLeftCommand(LeftCommand leftCommand) {
        this.leftCommand = leftCommand;
    }

    public void setRightCommand(RightCommand rightCommand) {
        this.rightCommand = rightCommand;
    }

    public void toLeft(){
        leftCommand.execute();
    }

    public void toRight(){
        rightCommand.execute();
    }
}
