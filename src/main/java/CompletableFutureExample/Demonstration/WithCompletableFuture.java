package CompletableFutureExample.Demonstration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WithCompletableFuture {
    static AtomicInteger atomicValue = new AtomicInteger(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //TODO note it's a list of CompletableFuture and NOT Future
        List<CompletableFuture> completableFutureList = new ArrayList<>();
        try {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                int currentValue = atomicValue.addAndGet(1);
                //TODO note it's a reference of CompletableFuture and NOT Future
                CompletableFuture future =  CompletableFuture.supplyAsync(() -> getFactorial(currentValue))
                        .thenApplyAsync((factorialValue) -> getSquareRoot(factorialValue));
                completableFutureList.add(future);
            }
            //TODO note how this is done.
            completableFutureList.forEach(CompletableFuture::join);
            long end = System.currentTimeMillis();
            System.out.println("Total Time Taken " + (end - start));
        } catch (Exception e) {
            System.out.println("Some Exception " + e);
        }
    }

    public static Integer getFactorial(int initialValue){
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            System.out.println("Exception while sleeping");
        }
        long factorial= 1;
        int num=1;
        while(num<=initialValue){
            factorial=factorial*num;
            num++;
        }
        int result = (int)factorial;
        System.out.println("For initialValue "+ initialValue+" got Factorial value= "+result);
        return result ;
    }

    public static Integer getSquareRoot(int initialValue) {

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Exception while sleeping");
        }

        int result = (int) Math.sqrt(initialValue);
        System.out.println("For Factorial Value " + initialValue + " got SquareRoot value= " + result);
        return result;
    }

}
