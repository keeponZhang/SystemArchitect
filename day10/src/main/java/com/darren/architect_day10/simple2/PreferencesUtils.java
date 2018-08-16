package com.darren.architect_day10.simple2;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hcDarren on 2017/9/24.
 */

public class PreferencesUtils {
    private volatile static PreferencesUtils mInstance;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private PreferencesUtils(){

    }

    public void init(Context context){
        mPreferences = context.getApplicationContext().getSharedPreferences("cache",Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static PreferencesUtils getInstance(){
        if(mInstance == null){
            synchronized (PreferencesUtils.class){
                if(mInstance == null){
                    mInstance = new PreferencesUtils();
                }
            }
        }
        return mInstance;
    }

    public PreferencesUtils saveString(String key,String value){
        mEditor.putString(key,value);
        return this;
    }

    public void commit(){
        mEditor.commit();
    }

    public String getString(String key){
        return mPreferences.getString(key,"");
    }
}
