package com.sergeyvolkodav.deferredcallback;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Demo {

    private static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws InterruptedException {

        Set<Thread> allThreads = new HashSet<>();
        final DeferredCallback deferredCallbackExecutor = new DeferredCallback();

        Thread service = new Thread(() -> {
            try {
                deferredCallbackExecutor.start();
            } catch (InterruptedException ie) {
            }
        });

        service.start();

//        for (int i = 0; i < 10; i++) {
//            Thread thread = new Thread(() -> {
//                CallBack cb = new CallBack(1, "Hello this is " + Thread.currentThread().getName());
//                deferredCallbackExecutor.registerCallback(cb);
//            });
//
//            thread.setName("Thread_" + (i + 1));
//            thread.start();
//            allThreads.add(thread);
//            Thread.sleep((random.nextInt(3) + 1) * 1000);
//        }

        Thread thread1 = new Thread(() -> {
            CallBack cb = new CallBack(8, "Hello this is " + Thread.currentThread().getName());
            deferredCallbackExecutor.registerCallback(cb);
        });
        thread1.start();
        allThreads.add(thread1);
        Thread.sleep( 500);


        Thread thread2 = new Thread(() -> {
            CallBack cb = new CallBack(6, "Hello this is " + Thread.currentThread().getName());
            deferredCallbackExecutor.registerCallback(cb);
        });
        thread2.start();
        allThreads.add(thread2);
        Thread.sleep( 500);

        Thread thread3 = new Thread(() -> {
            CallBack cb = new CallBack(4, "Hello this is " + Thread.currentThread().getName());
            deferredCallbackExecutor.registerCallback(cb);
        });
        thread3.start();
        allThreads.add(thread3);
        Thread.sleep( 500);


        for (Thread t : allThreads) {
            t.join();
        }
    }
}
