package com.sergeyvolkodav.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActualSemaphore {

    private static int slots = 3;
    static Semaphore semaphore = new Semaphore(slots);


    public static void main(String[] args) {

        ExecutorService es = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            es.execute(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("Acquired the permit for "+ finalI);
                    Thread.sleep(2000);
                    semaphore.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        }

    }
}
