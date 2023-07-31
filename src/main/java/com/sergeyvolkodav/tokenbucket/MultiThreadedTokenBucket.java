package com.sergeyvolkodav.tokenbucket;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedTokenBucket {

    private int possibleTokens;
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
        thread.start();
    }

    private void executeDeamonThread() {

        while (true) {

            synchronized (this) {

                //todo note here possibleTokens+1 is what we are checking
                // bcoz we need to check +1 value before incrementing
                if (possibleTokens+1 < MAX_TOKENS) {
                    possibleTokens = possibleTokens+1;
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

            while (possibleTokens == 0) { //todo note this needs to be a while loop and NOT an IF statement
                this.wait();
            }
            possibleTokens--;
        }

        System.out.println(
                "Granting " + Thread.currentThread().getName() + " token at " + System.currentTimeMillis() / ONE_SECOND+" possible tokens "+possibleTokens);

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
