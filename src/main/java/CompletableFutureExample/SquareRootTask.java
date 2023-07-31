package CompletableFutureExample;

import java.util.concurrent.Callable;

public class SquareRootTask implements Callable<Integer> {
    public int initialValue;

    public SquareRootTask(Object initialValue) {
        this.initialValue = (int)initialValue;
    }


    @Override
    public Integer call() throws Exception {
        Thread.sleep(1000);

        int result = (int) Math.sqrt(initialValue);
        System.out.println("For Factorial Value " + initialValue + " got SquareRoot value= " + result);
        return result;
    }
}
