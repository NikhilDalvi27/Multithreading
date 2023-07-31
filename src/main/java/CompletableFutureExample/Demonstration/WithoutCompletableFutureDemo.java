package CompletableFutureExample.Demonstration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class WithoutCompletableFutureDemo {
    static AtomicInteger atomicValue = new AtomicInteger(1);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                int currentValue = atomicValue.addAndGet(1);
                Future future = executorService.submit(new FactorialTask(currentValue));
                Integer factorialValue = (Integer) future.get();
                future = executorService.submit(new SquareRootTask(factorialValue));
                Integer squareRootValue =  (Integer)future.get();
            }
            long end    = System.currentTimeMillis();
            System.out.println("Total Time Taken "+(end-start));
        }catch (Exception e){
            System.out.println("Some Exception "+ e);
        }
    }



}

