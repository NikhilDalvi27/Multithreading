package com.sergeyvolkodav.readwritelock;

public class ReadWriteLock {

    private int readers = 0;
    private boolean isWriteLocked = false;


    //todo note this is synchronized bcoz
    // although multiple readers can acquire read lock,
    // they should not do so concurrently
    // bcoz readers variable is updated
    public synchronized void acquireReadLock() throws InterruptedException {

        //todo note this needs to be a while loop
        // instead of if condition,
        // to handle spurious wakeup
        while (isWriteLocked) {
            wait();
        }
        readers++;
    }

    public synchronized void releaseReadLock() throws InterruptedException {

        //todo note we cannot release more than we acquired
        while (readers == 0) {
            wait();
        }
        readers--;

        //todo note, notify should be called
        // if there's a possibility of some other thread
        // to stop waiting
        // due to the operations done by current thread
        notify(); /** Required for the while condition (readers!=0) in acquireWriteLock() method
                      else some other thread will forever be stuck in that while loop
                     even if readers==0 **/
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        while (isWriteLocked || readers != 0) {
            wait();
        }
        isWriteLocked = true;
    }

    public synchronized void releaseWriteLock() {
        isWriteLocked = false;

        //todo note, notify should be called
        // if there's a possibility of some other thread
        // to stop waiting
        // due to the operations done by current thread
        notify();/** Required for the while condition (isWriteLocked) in acquireReadLock() method
                     else some thread will forever be stuck in that while loop
                     even if isWriteLocked==false **/
    }
}
