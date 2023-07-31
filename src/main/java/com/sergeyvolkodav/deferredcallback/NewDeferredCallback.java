package com.sergeyvolkodav.deferredcallback;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NewDeferredCallback {

    private PriorityQueue<CallBack> priorityQueue = new PriorityQueue<>(new Comparator<CallBack>() {
        @Override
        public int compare(CallBack o1, CallBack o2) {
            return Math.toIntExact(o1.getExecuteAt() - o2.getExecuteAt());
        }
    });

    //todo, this lock is used for guarding the critical section
    private ReentrantLock lock = new ReentrantLock();

    //todo since we will be using condition variable for signalling
    // we will be using await and signal
    // instead of wait and notify
    private Condition condition = lock.newCondition();

    private long findSleepDuration(){
        long currentTime = System.currentTimeMillis();
        return priorityQueue.peek().getExecuteAt() - currentTime;
    }

    public void start() throws InterruptedException {
        long sleepFor = 0;

        while (true) { /**  Will keep on running, will go to sleep for the duration of the earliest callback **/
            lock.lock();

            while (priorityQueue.size() == 0) { /** Give up the lock and wait till the Queue is Empty, NOTE -> thread will have to re-acquire the lock again */
                condition.await(); /** unlock the lock which is acquired above, and put the current thread which acquired the lock to sleep **/
            }

            while (priorityQueue.size() != 0) {

                sleepFor = findSleepDuration();

                /**
                 * If the earliest callback in the queue is scheduled now for execution
                 * then, break the current loop and execute the earliest callback
                 */
                if(sleepFor<=0){
                    break;
                }

                /**
                 * else again wait till the time when earliest callback
                 * will be due for execution
                 */

                condition.await(sleepFor, TimeUnit.MILLISECONDS);
            }


            /** NOTE here we are executing callback, only when callback is due, sleepFor<=0  **/
            CallBack callBack = priorityQueue.poll();
            System.out.println("Executed at " + System.currentTimeMillis() / 1000
                    + " required at " + callBack.getExecuteAt() / 1000
                    + " message " + callBack.getMessage());


            lock.unlock();
        }
    }

    public void registerCallback(CallBack callBack) {
        lock.lock();
        priorityQueue.add(callBack);

        /** todo this is IMP Signal to wake up and notify the execution thread
         * that a new callback was added, so accordingly update sleep time
         * considering the new callback added
         *  **/
        condition.signal(); /** will wake up a single thread which is waiting on the condition variable
         which in our case is the executor thread **/


        lock.unlock(); /** This is necessary to release the lock, although we have signalled the waiting thread already **/
    }

}
