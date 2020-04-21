package com.darren.architect_day01.simple4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.darren.architect_day01.R;

import java.util.HashMap;
import java.util.Map;

public class Simple4MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_gson);

        /********************访问网络开始*******************/
        Map<String, Object> params = new HashMap<>();
        // 特定参数
        params.put("iid", 6152551759L);
        params.put("aid", 7);

        /*HttpUtils httpUtils = new HttpUtils();
        httpUtils.get(this, ConstantValue.UrlConstant.HOME_DISCOVERY_URL,
                params,new HttpCallBack<DiscoverListResult>() {
                    @Override
                    public void onSuccess(DiscoverListResult result) {
                        if (result.isOK()) {
                            // 没有列表数据的情况, 打印 Toast 或者做一些其他处理
                        } else {
                            // 有数据列表的情况，显示列表
                            showListData(result);
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {

                    }
                },true);*/
        HttpUtils.with(this).cache(true).get().param("iid", 6152551759L).param("aid", 7).request(
                new HttpCallBack<DiscoverListResult>() {
                    @Override
                    public void onSuccess(DiscoverListResult result) {
                        if (result.isOK()) {
                            // 没有列表数据的情况, 打印 Toast 或者做一些其他处理
                        } else {
                            // 有数据列表的情况，显示列表
                            showListData(result);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
        /********************访问网络结束*******************/
    }

    private void showListData(DiscoverListResult discoverListResult) {

    }


}
