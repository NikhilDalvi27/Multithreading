package com.sergeyvolkodav.uberride;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class UberRide {

    private int republicans = 0;
    private int democrats = 0;

    private Semaphore demsWaiting = new Semaphore(0);
    private Semaphore repubsWaiting = new Semaphore(0);

    /**
     * barrier is used to wait till a total of 4 threads arrive
     */
    CyclicBarrier barrier = new CyclicBarrier(4);
    ReentrantLock lock = new ReentrantLock();

    public void seatDemocrat() throws InterruptedException, BrokenBarrierException {
        boolean riderLeader = false;
        lock.lock();

        democrats++;
        if (democrats == 4) {
            System.out.println("Called release 3");
            /**
             * These are the threads which are previously acquired
             * and are now waiting for the current thread to call release
             */
            demsWaiting.release(3);
            democrats -= 4;
            riderLeader = true;
        } else if (democrats == 2 && republicans >= 2) {
            System.out.println("Called release 1");
            demsWaiting.release(1);

            repubsWaiting.release(2);

            riderLeader = true;
            democrats -= 2;
            republicans -= 2;
        } else {
            lock.unlock();

            /** Current thread will go to sleep
             * since initial permits are 0,
             * and it will be waked up
             * when other thread will call the release method
             * Check debug statements above
             */
            demsWaiting.acquire();
            System.out.println("After acquire");
        }
        seated();

        /** Note this is to make the threads wait
         *  till a total of 4 threads arrive here
         *  as defined in the barrier
         * **/
        barrier.await();

        if (riderLeader == true) {
            drive();
            lock.unlock();
        }
    }

    public void setRepublican() throws InterruptedException, BrokenBarrierException {
        boolean riderLeader = false;
        lock.lock();

        republicans++;
        if (republicans == 4) {
            repubsWaiting.release(3);
            republicans -= 4;
            riderLeader = true;
        } else if (republicans == 2 && democrats >= 2) {
            repubsWaiting.release(1);
            demsWaiting.release(2);
            riderLeader = true;
            democrats -= 2;
            republicans -= 2;
        } else {
            lock.unlock();
            repubsWaiting.acquire();
        }
        seated();
        barrier.await();
        if (riderLeader == true) {
            drive();
            lock.unlock();
        }

    }

    void seated() {
        System.out.println(Thread.currentThread().getName() + "  seated");
    }

    void drive() {
        System.out.println("Uber Ride on Its way... with ride leader " + Thread.currentThread().getName());
    }
}

