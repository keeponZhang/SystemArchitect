package com.darren.architect_day16;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * description:
 * author: Darren on 2017/10/11 11:16
 * email: 240336124@qq.com
 * version: 1.0
 */
public class DarrenRetrofit {

    public <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 1. 先做一下打印，获取方法名和参数
                /*Log.e("Method", method.getName());
                for (Object arg : args) {
                    Log.e("ARGS", arg+"");
                }*/

                // 2.解析方法注解参数到底是什么提交（Post） - 注解的不太懂请看前面的文章
                Annotation[] methodAnnotations = method.getAnnotations();
                for (Annotation methodAnnotation : methodAnnotations) {
                    // Post Get Multipart FormUrlEncoded 等等
                }

                // 3.解析 args 参数的注解


                // 4.封装成对象返回

                return null;
            }
        });
    }
}
