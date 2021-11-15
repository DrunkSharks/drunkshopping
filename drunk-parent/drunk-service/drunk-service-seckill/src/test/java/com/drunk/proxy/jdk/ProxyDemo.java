package com.drunk.proxy.jdk;

import java.lang.reflect.Proxy;

public class ProxyDemo {

    public static void main(String[] args) {
        Dog dog = new Dog();
        MyInvocationHandle invocationHandle = new MyInvocationHandle(dog);
        //获取代理对象
        Animal animal = (Animal) Proxy.newProxyInstance(Dog.class.getClassLoader(), Dog.class.getInterfaces(), invocationHandle);
        //调用代理方法
        animal.eat();
    }
}
