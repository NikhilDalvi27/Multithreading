package com.sergeyvolkodav.semaphore;

public class Semaphore {

    private int permits;
    private int maxCount;


    public Semaphore(int maxCount) {
        this.maxCount = maxCount;
    }

    public synchronized void acquire() throws InterruptedException {
        while (permits == maxCount) {
            wait();
        }

        //todo this can be called after notify also, bcoz it would be the same thing
        // till the current thread has acquired the lock
        permits++;

        //todo this is for any waiting thread in the release method is able to move forward,
        // since permits are now being increased
        notify();
    }

    public synchronized void release() throws InterruptedException {
        while (permits == 0) {
            wait();
        }

        //todo this can be called after notify also, bcoz it would be the same thing
        // till the current thread has acquired the lock
        permits--;

        //todo this is for any waiting thread in the release method is able to move forward,
        // since permits are now being decreased
        notify();
    }
}
