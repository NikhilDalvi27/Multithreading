package com.sergeyvolkodav.AwaitAndSignal;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitSignalExample {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean flag;
    private static int counter = 0;

    public static void main(String[] args) {
        new AwaitSignalExample().runExample();
    }

    private void runExample() {

        Thread incrementer = new Thread(() -> {
            lock.lock();
            try {
                while (flag) {
                    condition.await();
                }

                counter++;
                System.out.println("Incremented counter to " + counter);
                if(counter==10){
                    flag=true;
                }
            } catch (Exception e) {
                System.out.println("Some Exception");
            } finally {
                lock.unlock();
            }

        });

        Thread decrementer = new Thread(() -> {
            lock.lock();
            try {
                while (flag) {
                    condition.await();
                }

                counter--;
                System.out.println("Decremented counter to " + counter);
                if(counter<0){
                    flag=true;
                }
            } catch (Exception e) {
                System.out.println("Some Exception");
            } finally {
                lock.unlock();
            }

        });

    }
}
