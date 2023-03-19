package com.sergeyvolkodav.volatileExample;

import java.util.concurrent.ThreadLocalRandom;

public class VolatileExample {


    volatile int num = 0;
     int sharedVar1 = 0;
     int sharedVar2 = 0;


    void someMethod() throws InterruptedException {
//        Thread readerThread = new Thread(()->{
//            int i=0;
//            while (i<100){
//                System.out.println("Read num= "+num+" sharedVar1= "+sharedVar1+" sharedVar2= "+sharedVar2);
//                i++;
//            }
//        });

        Thread writerThread1 = new Thread(()->{
            int i=0;
            while (i<10000){
                //todo note this can't be solved using volatile,
                // but needs to be solved using synchronized

                num++;
                sharedVar1++;
                sharedVar2++;
                i++;
            }
        });

        Thread writerThread2 = new Thread(()->{
            int i=0;
            while (i<10000){
                num++;
                sharedVar1++;
                sharedVar2++;
                i++;
            }
        });

//        readerThread.start();
        writerThread1.start();
        writerThread2.start();
        writerThread2.join();
        writerThread1.join();
        System.out.println("num= "+num+" sharedVar1= "+sharedVar1+" sharedVar2= "+sharedVar2);

    }
    public static void main(String[] args) throws InterruptedException {

      VolatileExample volatileExample = new VolatileExample();
      volatileExample.someMethod();

    }

}
