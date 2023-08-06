package com.sergeyvolkodav.RateLimiter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MultiThreadedTokenBucket {

    private int currentTokens;
    private final int MAX_TOKENS;
    private final int ONE_SECOND = 1000;


    public MultiThreadedTokenBucket(int max_tokens) {
        MAX_TOKENS = max_tokens;

        //todo note, daemon thread started in the constructor,
        // this is not the correct way,
        // bcoz the thread calling the constructor might
        // receive NOT-YET-FULLY constructed object using this keyword
        // use a factory pattern here to recieve a fully constructed object

        Thread thread = new Thread(() -> {
            executeDeamonThread();
        });
        thread.setDaemon(true);
        thread.start(); //todo cannot use thread.join() here, bcoz the daemon thread keeps on running.
    }

    private void executeDeamonThread() {

        while (true) {

            synchronized (this) {

                //todo note here currentTokens+1 is what we are checking
                // bcoz we need to check +1 value before incrementing
                if (currentTokens +1 < MAX_TOKENS) {
                    currentTokens = currentTokens +1;
                }

                //todo note daemon threads notifies other threads when the tokens are available
                this.notify();
            }

            try {
                //todo note the daemon thread sleeps for 1 second
                Thread.sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getToken() throws InterruptedException {

        synchronized (this) {

            //todo note we are waiting till the tokens are not available

            //todo note this needs to be a while loop and NOT an IF statement
            while (currentTokens == 0) {

                //todo note, we are using wait() here instead of sleep bcoz,
                // we are notifying from the deamon thread that token is available.
                // NOTE, here the waiting thread, will give up the Lock as soon as it calls wait()
                // allowing other threads to acquire the lock.
                this.wait();
            }
            currentTokens--;
            System.out.println(
                    "Granting " + Thread.currentThread().getName() + " token at " + System.currentTimeMillis()/ONE_SECOND  +" currentTokens= "+ currentTokens);
        }



    }
}

class Demonstration{

    public static void main(String[] args) throws InterruptedException {

        MultiThreadedTokenBucket tokenBucket = new MultiThreadedTokenBucket(5);

        //todo this is to simulate the queue is already filled with 5 tokens
        Thread.sleep(10*1000);

        List<Thread>allThreads = new ArrayList<>();
        for(int i=0;i<15;i++){
            Thread thread = new Thread(()->{
                try {
                    tokenBucket.getToken();
                } catch (InterruptedException e) {
                    System.out.println("Some problem encountered");
                    throw new RuntimeException(e);
                }
            });
            thread.setName("Thread_"+(i+1));
            allThreads.add(thread);
        }

        for(Thread thread : allThreads){
            thread.start();
        }

        for(Thread thread : allThreads){
            thread.join();
        }

    }
}
