package com.yanghaoyi.androidpdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yanghaoyi.androidpdemo.R;

import java.lang.reflect.Method;

/**
 * @author : YangHaoYi on 2018/8/1.
 *         Email  :  yang.haoyi@qq.com
 *         Description :在开发过程中，开发者如果调用了非 SDK 接口，会导致应用出现crash，无法启动；或在运行过程中出现崩溃、闪退等现象
 *         Change : YangHaoYi on 2018/8/1.
 *         Version : V 1.0
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener {

    private TextView tvToForeground;
    private TextView tvToBroadCast0;
    private TextView tvToBroadCast;
    private TextView tvToDialog;
    private TextView tvToServiceToActivity;
    private TextView tvToBackgroundStartService;
    private TextView tvToNotSDKPage;
    private TextView tvToNotSDKPage2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init(){
        initView();
        initEvent();
    }

    private void initView(){
        tvToBroadCast = findViewById(R.id.tvToBroadCast);
        tvToBroadCast0 = findViewById(R.id.tvToBroadCast0);
        tvToForeground = findViewById(R.id.tvToForeground);
        tvToDialog = findViewById(R.id.tvToDialog);
        tvToServiceToActivity = findViewById(R.id.tvToServiceToActivity);
        tvToBackgroundStartService = findViewById(R.id.tvToBackgroundStartService);
        tvToNotSDKPage = findViewById(R.id.tvToNotSDKPage);
        tvToNotSDKPage2 = findViewById(R.id.tvToNotSDKPage2);
    }

    private void initEvent(){
        tvToForeground.setOnClickListener(this);
        tvToBroadCast.setOnClickListener(this);
        tvToDialog.setOnClickListener(this);
        tvToServiceToActivity.setOnClickListener(this);
        tvToBackgroundStartService.setOnClickListener(this);
        tvToNotSDKPage.setOnClickListener(this);
        tvToBroadCast0.setOnClickListener(this);
        tvToNotSDKPage2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.tvToForeground:
                intent = new Intent(HomeActivity.this,ForegroundActivity.class);
                startActivity(intent);
                break;
            case R.id.tvToBroadCast0:
                intent = new Intent();
                intent.setAction(BroadCastActivity.TOAST_ACTION);
                sendBroadcast(intent);
                Log.e("TAG", "HomeActivity onClick sendBroadcast:");
                break;
            case R.id.tvToBroadCast:
                intent = new Intent(HomeActivity.this,BroadCastActivity.class);
                startActivity(intent);
                break;
            case R.id.tvToDialog:
                intent = new Intent(HomeActivity.this,DialogActivity.class);
                startActivity(intent);
                break;
            case R.id.tvToServiceToActivity:
                intent = new Intent(HomeActivity.this,ServiceToActivity.class);
                startActivity(intent);
                break;
            case R.id.tvToBackgroundStartService:
                intent = new Intent(HomeActivity.this,BackGroundStartServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.tvToNotSDKPage:
                intent = new Intent(HomeActivity.this,NotSDKInterfaceActivity.class);
                startActivity(intent);
                break;
            case R.id.tvToNotSDKPage2:
                testProperties();
                break;
            default:

                break;
        }
    }

    private void testProperties() {
        hasNotchForMIUI();
    }
    private  boolean hasNotchForMIUI() {
        return getIntProperty("ro.miui.notch", 0) == 1;
    }
    private  int getIntProperty(String key, int defaultValue) {
        int value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("getInt", String.class, int.class);
            value = (int) (get.invoke(c, key, defaultValue));
            Log.e("TAG", "HomeActivity getIntProperty value:"+value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "HomeActivity getIntProperty 抛异常了:");
        } finally {
            return value;
        }
    }
}
