package com.sergeyvolkodav.readwritelock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ActualReadWriteLock {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private Condition condition1 = readLock.newCondition();
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private Condition condition2 = writeLock.newCondition();
    
    
    public void readResource() {
        readLock.lock();
        try {

        } finally {
            readLock.unlock();
        }
    }

    public void writeResource() {
        writeLock.lock();
        try {

        } finally {
            writeLock.unlock();
        }
    }
}
