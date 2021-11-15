package com.drunk.seckill;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DeadLockDemo implements Runnable{
    private String lockA;
    private String lockB;

    public DeadLockDemo(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName()+"获取"+lockA);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (lockB){
                System.out.println(Thread.currentThread().getName()+"获取"+lockB);
            }
        }
    }

    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }
        });
        set.add(1);
        set.add(3);
        set.add(2);
        set.add(200);
        set.add(165);
        set.add(185);

        Iterator<Integer> iterator = set.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }

        new HashSet<>().add("1");
        new ArrayList<>().add("");

        new CopyOnWriteArrayList();
        //单例线程池LinkedBlockingQueue
        Executors.newSingleThreadExecutor();
        //固定线程池LinkedBlockingQueue
        Executors.newFixedThreadPool(10);
        //缓存线程池SynchronousQueue
        Executors.newCachedThreadPool();
        //定时器线程池DelayedWorkQueue
        Executors.newScheduledThreadPool(5);

        Iterator<Object> iterator1 = new HashSet<>().iterator();
        int next = (int) iterator1.next();
    }
}
