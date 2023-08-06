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
                    for(int i=0;i<1000;i++) {
                        System.out.println("I am too sleepy... Let me sleep for an hour.");

                        //todo this is ideally how we need to continuously poll and check for interrupt flag
                        // to 1. kill the thread as soon as interrupt flag is set
                        // and 2. to kill the current thread safely (close database connection, rollback half operations etc.)
                        if (Thread.currentThread().isInterrupted()) {
                            System.out.println("Stopping the task as sleepy thread is interrupted!!");
                            System.out.println("Initial  interrupt flag value : "+ Thread.currentThread().isInterrupted());

                            throw new InterruptedException();
                        }
                    }
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException ie) {
                    //note this flag is cleared once the interrupted exception is thrown
                    //todo here we are just checking the interrupt flag value and not clearing it.
                    System.out.println("The interrupt flag is cleared : "+ Thread.currentThread().isInterrupted());

                    //todo again set the interrupt flag value
                    Thread.currentThread().interrupt();
                    System.out.println("Oh someone woke me up ! ");

                    //try using  Thread.currentThread().isInterrupted() here to see the difference
                    System.out.println("The interrupt flag is set now : " + Thread.interrupted());
                    System.out.println("The interrupt flag is cleared 2 : "+Thread.interrupted());

                } }
        });
        sleepyThread.start();
        Thread.sleep(1);
        System.out.println("About to wake up the sleepy thread ...");
        sleepyThread.interrupt();
        System.out.println("Woke up sleepy thread ...");
        sleepyThread.join();
    }

}