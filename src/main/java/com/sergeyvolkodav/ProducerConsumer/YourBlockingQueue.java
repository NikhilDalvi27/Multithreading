package com.sergeyvolkodav.ProducerConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A blocking queue is defined as a queue which blocks the caller of the enqueue method
 * if there's no more capacity to add the new item being enqueued.
 * Similarly, the queue blocks the dequeue caller if there are no items in the queue.
 * Also, the queue notifies a blocked enqueuing thread when space becomes available
 * and a blocked dequeuing thread when an item becomes available in the queue
 */
public class YourBlockingQueue {

}

class CustomQueue {


    private Object arr[];
    private int size;
    private int currentSize;
    private int head,tail;

    //todo note, this is declared final, so as not to allow modifications to it.
    final Object lock = new Object();

    CustomQueue(int size){
        arr = new Object[size];
        this.size = size;
        currentSize=0;
        head=0;
        tail=0;
    }

    public void printQueue() {
        int i = head;
        int count = 0;
        while (count != currentSize) {
            System.out.print(arr[i] + " ");
            i = (i + 1) % size;
            count++;
        }
        System.out.println();
    }
    public void enqueue(Object o) throws InterruptedException {
        //todo note this synchronized is to allow only 1 thread to call enqueue/dequeue method
        // so that at a time only 1 thread can manipulate the queue

        synchronized (lock) { //todo note we are acquiring lock here before calling wait() and notify()

            //todo note here for checking predicate
            // if condition is changed to while loop
            System.out.println("Current Size :"+currentSize);
            while (currentSize == size) {
                lock.wait();
            }
            currentSize++;
            arr[tail] = o;
            tail = (tail + 1) % size;

            //todo this is imp to call notifyAll instead of notify
            // for the edge case where the queue size is 1,
            // if there are 2 producer thread and 1 consumer thread
            // P2 and C1 are sleeping
            // then P1 adds an item (queue capacity = 1 ) and calls notify
            // if suppose ONLY P2 is woken up, instead of C1 (because of notify, instead of notifyAll)
            // then P2 will get blocked on line 52
            // and there is no one to wake up C1
            // Hence notifyAll ensures, in such cases, C1 is also woken up.


            //todo note, this is needed since we are changing the currentSize variable
            // which is a predicate for both enqueue/dequeue threads
            lock.notifyAll();
        }
    }

    public Object dequeue() throws InterruptedException {
        //todo note this synchronized is to allow only 1 thread to call enqueue/dequeue method
        // so that at a time only 1 thread can manipulate the size variable

       synchronized (lock) { //todo note we are acquiring lock here before calling wait() and notify()

           //todo note here for checking predicate
           // if is changed to while loop
           System.out.println("Current Size :"+currentSize);
           while (currentSize == 0) {
              lock.wait();
           }
           Object temp = arr[head];
           arr[head] = null;
           currentSize--;
           head = (head + 1) % size;
           //todo note, this is needed since we are changing the currentSize variable
           // which is a predicate for both enqueue/dequeue threads
           lock.notifyAll();
           return temp;
       }
    }

    public static void main(String[] args) {
        ExecutorService executorService1 = Executors.newFixedThreadPool(4);
        ExecutorService executorService2 = Executors.newFixedThreadPool(4);

        CustomQueue blockingQueue = new CustomQueue(4);

        for(int i=0;i<50;i++){

            int finalI = i;
            executorService1.submit(()->{
                try {
                    blockingQueue.enqueue(finalI);
                    System.out.println("Enqueued "+finalI);
                } catch (InterruptedException e) {
                    System.out.println("Some Exception!! "+e);
                    throw new RuntimeException(e);
                }
            });
        }

        for(int j=0;j<50;j++){
            //todo figure out why can't we use executorService1 itself here
            executorService2.submit(()->{
                try {
                   Object result = blockingQueue.dequeue();
                    System.out.println("Dequeued "+result);
                } catch (InterruptedException e) {
                    System.out.println("Some Exception!! "+e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

}