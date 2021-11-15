package com.drunk.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandle implements InvocationHandler {

    //代理对象
    private Object target;

    public MyInvocationHandle(Object target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理前置增强");
        Object result = method.invoke(target, args);
        System.out.println("代理后置增强");
        return result;
    }
}
