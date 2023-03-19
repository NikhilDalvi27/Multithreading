package com.sergeyvolkodav.deferredcallback;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallback {

    private PriorityQueue<CallBack> priorityQueue = new PriorityQueue<>(new Comparator<CallBack>() {
        @Override
        public int compare(CallBack o1, CallBack o2) {
            return Math.toIntExact(o1.getExecuteAt() - o2.getExecuteAt());
        }
    });
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void start() throws InterruptedException {
        long sleepFor = 0;
        long lastSeen = 0;/** To check if any new Callback was added OR NOT **/

        while (true) { /**  Will keep on running, will go to sleep for the duration of the earliest callback **/
            lock.lock();

            while (priorityQueue.size() == 0) { /** Give up the lock and wait till the Queue is Empty, NOTE -> thread will have to re-acquire the lock again */
                condition.await(); /** unlock the lock which is acquired above, and put the current thread which acquired the lock to sleep **/
            }

            /** priorityQueue size will be 1, when the first callback is added
             * lastSeen will be 0 at that time, but lastSeen will be updated to 1 after executing below code (line 50)
             * and in the 2nd iteration below condition will be true
             * Indicating thread will sleepFor the time duration of most recent callback considering all added callbacks
             *
             * **/
            if (lastSeen == priorityQueue.size()) {  /** NOTE --> True when NO new callbacks are added  **/
                condition.await(sleepFor, TimeUnit.MILLISECONDS);
            }else{
                System.out.println("JUST FOR DEBUGGING "+priorityQueue.size());
            }

            long currentTime = System.currentTimeMillis();
            while (priorityQueue.size() != 0 && currentTime >= priorityQueue.peek().getExecuteAt()) { /** 2nd cond will be false if the callback is not due yet **/

                /** NOTE here we are executing callback, only when callback is due, as per the 2nd condition **/
                CallBack callBack = priorityQueue.poll();
                System.out.println("Executed at " + System.currentTimeMillis() / 1000
                        + " required at " + callBack.getExecuteAt() / 1000
                        + " message " + callBack.getMessage());
            }
            sleepFor = priorityQueue.size() == 0 ? 0 : priorityQueue.peek().getExecuteAt() - currentTime;
            lastSeen = priorityQueue.size();

            lock.unlock();
        }
    }

    public void registerCallback(CallBack callBack) {
        lock.lock();
        priorityQueue.add(callBack);

        /** Signal to wake up and notify the execution thread
         * that a new callback was added, so accordingly update sleep time
         * considering the new callback added
         *  **/
        condition.signal(); /** will wake up a single thread which is waiting on the condition variable**/
        lock.unlock(); /** This is necessary to release the lock, although we have signalled the waiting thread **/
    }

}
