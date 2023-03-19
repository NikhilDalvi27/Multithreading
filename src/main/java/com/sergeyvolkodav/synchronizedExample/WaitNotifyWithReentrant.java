package com.sergeyvolkodav.synchronizedExample;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WaitNotifyWithReentrant {

    public static void main(String[] args) {
        ReentrantLock lock1 = new ReentrantLock();
        Condition condition1 = lock1.newCondition();

        ReentrantLock lock2 = new ReentrantLock();
        Condition condition2 = lock2.newCondition();

        Thread t1  = new Thread(()->{
           try {
               lock1.lock();
               System.out.println("From thread 1, before calling await");
               condition1.await(); //todo note this is await and not wait
               System.out.println("From thread 1, after calling await");

           } catch (InterruptedException e) {
               System.out.println("Interrrupted exception from thread 1");

           } finally {
               lock1.unlock();
           }
        });

        Thread t2  = new Thread(()->{
            try {
                lock1.lock();
                System.out.println("From thread 2, before calling notify");

                // todo note this will throw an exception since
                //  condition2 belongs to a different lock (lock2 and not lock1)
//                condition2.signal();
                condition1.signal();
                System.out.println("From thread 2, after calling notify");
            } finally {
                lock1.unlock();
            }
        });


        t1.start();
        t2.start();

    }
}
