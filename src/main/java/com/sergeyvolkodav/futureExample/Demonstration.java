package com.sergeyvolkodav.futureExample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Demonstration {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        List<Future> futureList = new ArrayList<>();

        for(int i=0;i<10;i++) {
            int finalI = i;

            //todo note we are passing callable in the submit method
            Future future = executor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Exception occurred");
                }
                return finalI *10;
            });
            futureList.add(future);
        }

        for(Future future:futureList){
            System.out.println("Check this "+future.get());
        }
        executor.shutdown();
    }
}
