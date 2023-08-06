package com.sergeyvolkodav.RateLimiterCustomerSpecific;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {

    //CustomerId-->CurrentToken Map.
    private HashMap<Integer, Integer> customerIdToCurrentTokenMap;
    private final int MAX_TOKENS;
    private final int ONE_SECOND = 1000;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();


    public RateLimiter(int max_tokens) {
        MAX_TOKENS = max_tokens;

        //todo note, daemon thread started in the constructor,
        // this is not the correct way,
        // bcoz the thread calling the constructor might
        // receive NOT-YET-FULLY constructed object using this keyword
        // use a factory pattern here to recieve a fully constructed object

        customerIdToCurrentTokenMap = new HashMap<>();
        Thread thread = new Thread(() -> {
            executeDeamonThread();
        });
        //todo note we need to set this thread to be daemon thread.
        thread.setDaemon(true);
        thread.start(); //todo cannot use thread.join() here, bcoz the daemon thread keeps on running.
    }

    private void executeDeamonThread() {

        while (true) {

            lock.lock();
            try {

                for (int customerId : customerIdToCurrentTokenMap.keySet()) {
                    //todo note here currentTokens+1 is what we are checking
                    // bcoz we need to check +1 value before incrementing
                    int currentTokens = customerIdToCurrentTokenMap.get(customerId);
                    if (currentTokens + 1 <= MAX_TOKENS) {
                        currentTokens = currentTokens + 1;
                    }
                    customerIdToCurrentTokenMap.put(customerId, currentTokens);
                }

                //todo check if this Map gets updated after adding the keys for customers
                System.out.println("Refilled bucket" + customerIdToCurrentTokenMap);


                //todo note daemon threads notifies other threads when the tokens are available
                condition.signalAll();

            } finally {
                //todo sleep only after giving up the LOCK
                lock.unlock();
            }


            try {
                //todo note the daemon thread sleeps for 1 second,
                // VVIMP, we cannot put the current thread to sleep
                // while we are owning the lock.
                // Hence cannot write this code in above try block where we have acquired the lock.
                Thread.sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getToken(int customerId) throws InterruptedException {

        lock.lock();

        try {


            //todo,if it's a NEW customer, then max_tokens should be available
            // since he has NOT made any request yet.
            // this is like initializing a bucket for that customer.
            if (!customerIdToCurrentTokenMap.containsKey(customerId)) {
                customerIdToCurrentTokenMap.put(customerId, MAX_TOKENS);
            }
            int currentTokens = customerIdToCurrentTokenMap.get(customerId);
            //todo note this needs to be a while loop and NOT an IF statement

            while (currentTokens == 0) {

                //todo note, we are using wait() here instead of sleep bcoz,
                // we are notifying from the deamon thread that token is available.
                // NOTE, here the waiting thread, will give up the Lock as soon as it calls wait()
                // allowing other threads to acquire the lock.
                condition.await();

                //todo NOTE this is VVIMP,
                // since we should update the currentToken with latest value
                // after waiting is done
                currentTokens = customerIdToCurrentTokenMap.get(customerId);
            }

            //todo this is simulating that
            // current request consumed 1 token
            currentTokens--;
            customerIdToCurrentTokenMap.put(customerId, currentTokens);

            System.out.println(
                    "Granting customerId " + customerId + " token at " + System.currentTimeMillis() / ONE_SECOND + " currentTokens= " + currentTokens);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter(5);

        Thread customer1 = new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                try {
                    rateLimiter.getToken(1);
                    //todo this is firing request, after a delay of 2 secs
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread customer2 = new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                try {
                    //todo this is firing request continuously, without any delay
                    rateLimiter.getToken(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        customer1.start();
        customer2.start();

        customer1.join();
        customer2.join();

    }
}
