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
        Thread lateThread = new Thread(new Runnable() {
            public void run() {
                CallBack cb = new CallBack(8, "Hello this is the callback submitted first");
                deferredCallbackExecutor.registerCallback(cb);
            }
        });
        lateThread.start();
        Thread.sleep(3000);

        Thread earlyThread = new Thread(new Runnable() {
            public void run() {
            CallBack cb = new CallBack(1, "Hello this is callback sumbitted second");
            deferredCallbackExecutor.registerCallback(cb);
        } });
        earlyThread.start();

        lateThread.join();
        earlyThread.join();
    }
}
