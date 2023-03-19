package com.sergeyvolkodav.queue;

import java.util.Scanner;

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


    Object arr[];
    int size;
    int currentSize;
    int head,tail;

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
        // so that at a time only 1 thread can manipulate the size variable

        synchronized (lock) { //todo note we are acquiring lock here before calling wait() and notify()

            //todo note here for checking predicate
            // if is changed to while
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
            // if suppose P2 is woken up instead of C1
            // then P2 will get blocked on line 52
            // and there is no one to wake up C1


            lock.notifyAll();
        }
    }

    public Object dequeue() throws InterruptedException {
        //todo note this synchronized is to allow only 1 thread to call enqueue/dequeue method
        // so that at a time only 1 thread can manipulate the size variable

       synchronized (lock) { //todo note we are acquiring lock here before calling wait() and notify()

           //todo note here for checking predicate
           // if is changed to while
           while (currentSize == 0) {
              lock.wait();
           }
           Object temp = arr[head];
           arr[head] = null;
           currentSize--;
           head = (head + 1) % size;
           lock.notifyAll();
           return temp;
       }
    }

}