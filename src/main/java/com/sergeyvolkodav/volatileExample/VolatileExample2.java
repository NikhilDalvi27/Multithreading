package com.sergeyvolkodav.volatileExample;



//use of volatile keyword is to solve data race problem,
// all the code before volatile keyword will be executed before volatile declaration
// all the code after volatile keyword will be executed after volatile declaration
// there won't be instruction re-ordering

//volatile keyword declaration, can be only be used at class level, where static is defined
// volatile keyword cannot be used for a local variable

public class VolatileExample2 {

    //todo volatile keyword is mostly used to make the instance variable of the class thread safe,
    // this is needed when we want all the threads to read the most latest value of a particular variable

    //todo volatile keyword instructs the CPU to read the value from the main memory and not the local cache

    //todo note volatile is LOCK Free hence no guarantee of atomicity.

    //todo note, here if the sharedVariable is accessed by 2 threads running on different PROCESSOR (Core),
    // then changes made by 1 thread may not be instantly accessible to other thread leading to data inconsistency,
    // to propogate the change instantly, we use the volatile keyword

    static  volatile int sharedVariable = 0; //todo try removing and adding the volatile keyword

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            int localCounter = sharedVariable;

            while(localCounter<10){
                if(localCounter!=sharedVariable){
                    System.out.println("T1 local counter has changed");
                    localCounter = sharedVariable;
                }
            }
        });

        Thread t2  = new Thread(()->{
            int localCounter = sharedVariable;
            while(localCounter<10){
                System.out.println("T2 local counter incremented to "+(localCounter+1));
                sharedVariable = ++localCounter;

                try {
                    Thread.sleep(500);
                }catch (Exception e){
                    System.out.println(e);
                }

            }
        });

        t1.start();
        t2.start();
    }
}

