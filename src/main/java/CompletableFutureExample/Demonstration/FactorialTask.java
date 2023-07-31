package CompletableFutureExample.Demonstration;

import java.util.concurrent.Callable;

public class FactorialTask implements Callable<Integer> {

    int initialValue;
    public FactorialTask(int initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public Integer call() throws Exception {
        Thread.sleep(1000);
        long factorial= 1;
        int num=1;
        while(num<=initialValue){
            factorial=factorial*num;
            num++;
        }
        int result = (int)factorial;
        System.out.println("For initialValue "+ initialValue+" got squareRoot value= "+result);
        return result ;
    }

}
