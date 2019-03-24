package com.darren.architect_day16.simple3;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by hcDarren on 2017/10/15.
 */

public class DarrenRetrofit {

    public <T> T create(Class<T> clazz){
        // 动态代理
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Log.e("TAG",method.getName());

                        // 如果要实现 Retrofit 一样的代码应该怎么办？
                        // 1. 解析方法的所有注解 比如 POST GET FormUrlEncoded 等等

                        // 2. 解析参数的所有注解 比如 FieldMap Part PartMap 等等

                        // 3. 封装成 Call 对象

                        // 4.返回的 Call 对象
                        return "返回";
                    }
                });
    }
}
