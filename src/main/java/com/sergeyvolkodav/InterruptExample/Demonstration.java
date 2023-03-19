package com.sergeyvolkodav.InterruptExample;

import java.util.ArrayList;
import java.util.List;

public class Demonstration {
    public static void main(String[] args) throws InterruptedException {
        InterruptExample.example();
    }

}
class InterruptExample {
    static public void example() throws InterruptedException {

        System.out.println("From Main thread "+Thread.currentThread().getName());

        new Thread (()->{
            System.out.println("From thread "+Thread.currentThread().getName());
        }).start();

        final Thread sleepyThread = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("I am too sleepy... Let me sleep for an hour.");
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException ie) {
                    //note this flag is cleared once the interrupted exception is thrown
                    System.out.println("The interrupt flag is cleared : "+ Thread.currentThread().isInterrupted());
                    Thread.currentThread().interrupt();
                    System.out.println("Oh someone woke me up ! ");

                    //try using  Thread.currentThread().isInterrupted() here to see the difference
                    System.out.println("The interrupt flag is set now : " + Thread.interrupted());
                    System.out.println("The interrupt flag is cleared 2 : "+Thread.interrupted());

                } }
        });
        sleepyThread.start();
        System.out.println("About to wake up the sleepy thread ...");
        sleepyThread.interrupt();
        System.out.println("Woke up sleepy thread ...");
        sleepyThread.join();
    }

}