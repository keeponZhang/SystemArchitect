package com.darren.architect_day09;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.darren.architect_day09.navigationbar.DefaultNavigationBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup parent = (ViewGroup) findViewById(R.id.view_root);

        DefaultNavigationBar navigationBar =
                new DefaultNavigationBar.Builder(this,parent)
                        .setLeftText("返回")
                        .hideLeftText()
                        .setLeftClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        })
                        // 还有一些参数
                        .create();

        // 在写代码的时候一个是 高扩展 ，并不是要把所有的内容和出现的问题都想到，而是在新增加功能时候可以保证原来的代码不变
        // 对于开发者来说需要用好 最少知识原则，使用者是并不想关注太多

        // 如果你想设置其他属性呢？比如文字大小，比如文字颜色，设置图片？等等
        /*TextView textView = navigationBar.findViewById(R.id.back_tv);
        textView.setVisibility(View.GONE);*/
        // 还有就是有时候我们并不需要去关注 id 以及我们头部的样式，大部分情况下是类似，所以必须还要提供一个默认的


        // connectTimeout().addNetworkInterceptor().addInterceptor() 其实是要给OKHttp用的
        // 按照一般的套路基本都是 OkHttpClient client = new OkHttpClient();
        //                        client.setTimeOut();
        //                        client.setNetworkInterceptor();
        //                        client.setInterceptor();
        /*OkHttpClient client = new OkHttpClient.Builder().connectTimeout()
                .addNetworkInterceptor().addInterceptor().build();*/
    }
}
