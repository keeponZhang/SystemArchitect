package com.darren.architect_day16.simple2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by hcDarren on 2017/10/15.
 * 银行办理业务 - 动态代理 - InvocationHandler
 */

public class BankInvocationHandler implements InvocationHandler{
    /**
     * 被代理的对象
     */
    private Object mObject;

    public BankInvocationHandler(Object object){
        this.mObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 执行方法 ，目标接口调用的方法都会来到这里面
        // System.out.println("methodName = "+method.getName());
        System.out.println("开始受理");
        // System.out.println("params = "+args.toString());
        // 调用被代理对象的方法,这里其实调用的就是  man 里面的 applyBank 方法
        Object voidObject = method.invoke(mObject,args);
        System.out.println("操作完毕");
        return voidObject;
    }
}
