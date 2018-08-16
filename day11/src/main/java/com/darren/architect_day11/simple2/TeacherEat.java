package com.darren.architect_day11.simple2;

import android.util.Log;

/**
 * Created by hcDarren on 2017/9/30.
 */

public class TeacherEat implements Eat {
    private Eat eat;
    public TeacherEat(PersonEat eat){
        this.eat = eat;
    }
    @Override
    public void eat() {
        Log.e("TAG","喝个汤");
        Log.e("TAG","点个菜");
        eat.eat();
        Log.e("TAG","盘子不用送吃完走人");
    }
}
