package com.drunk.seckill;

import org.junit.Test;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class WeakReferenceDemo {

    public static void main(String[] args) {
        /*Object o = new Object();
        WeakReference wr = new WeakReference(o);
        //SoftReference sr = new SoftReference(o);

        System.out.println(o);
        System.out.println(wr.get());

        o = null;
        System.gc();
        System.out.println(o);
        System.out.println(wr.get());

        "1".intern();*/
        Thread thread = new Thread();
        thread.start();
        thread.start();
    }

}
