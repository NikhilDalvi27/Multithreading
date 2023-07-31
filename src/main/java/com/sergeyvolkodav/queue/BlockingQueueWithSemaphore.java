package com.sergeyvolkodav.queue;

public class BlockingQueueWithSemaphore<T> {
    T[] array;
    int currentSize = 0;
    int capacity;
    int head = 0;
    int tail = 0;
    CountingSemaphore semLock = new CountingSemaphore(1, 1);
    CountingSemaphore semProducer;
    CountingSemaphore semConsumer;
    @SuppressWarnings("unchecked")
    public BlockingQueueWithSemaphore(int capacity) {
        // The casting results in a warning
        array = (T[]) new Object[capacity];
        this.capacity = capacity;

        //todo All permits available since producer will initially produce something
        this.semProducer = new CountingSemaphore(capacity, capacity);

        //todo No permits available since consumer cannot consume anything initially
        this.semConsumer = new CountingSemaphore(capacity, 0);
    }
    public void enqueue(T item) throws InterruptedException {
        semProducer.acquire(); //todo, assume acquire means minus
        semLock.acquire(); //todo this is to ensure only 1 producer manipulates the queue

        array[tail] = item;
        tail = (tail+1) % capacity;
        currentSize++;

        semLock.release();
        semConsumer.release();  //todo, assume release means plus
    }

    public T dequeue() throws InterruptedException {
        T item = null;
        semConsumer.acquire(); //todo, assume acquire means minus
        semLock.acquire();

        item = array[head];
        array[head] = null;
        head = (head+1)%capacity;
        currentSize--;

        semLock.release();
        semProducer.release(); //todo, assume release means plus
        return item;
    }


    private class CountingSemaphore extends java.util.concurrent.Semaphore {
        public CountingSemaphore(int capacity, int availablePermits) {
            super(capacity);
            for(int i=0;i<capacity-availablePermits;i++){
                try {
                    this.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueueWithSemaphore blockingQueueWithSemaphore = new BlockingQueueWithSemaphore<Integer>(5);


        Thread t1 = new Thread(()->{
           for(int i=0;i<50;i++){
               try {
                   blockingQueueWithSemaphore.enqueue(i);
                   System.out.println("Enqueued from thread 1 "+i);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });

        Thread t2 = new Thread(()->{

            for(int i=0;i<25;i++){
                try {
                    System.out.println("Dequeued item from thread 2 "+ blockingQueueWithSemaphore.dequeue());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t3 = new Thread(()->{

            for(int i=0;i<25;i++){
                try {
                    System.out.println("Dequeued item from thread 3 "+ blockingQueueWithSemaphore.dequeue());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }



}