package com.drunk.proxy.cglib;

import org.springframework.cglib.proxy.Enhancer;

public class CglibDemo {

    public static void main(String[] args) {
        //Cglib核心工具类,字节码增强器
        Enhancer enhancer = new Enhancer();
        //设置代理对象
        enhancer.setSuperclass(Dog.class);
        //设置回调所需拦截器
        enhancer.setCallback(new MyMethodInterceptor());
        //创建代理对象
        Dog dog = (Dog) enhancer.create();
        dog.eat();
    }
}
