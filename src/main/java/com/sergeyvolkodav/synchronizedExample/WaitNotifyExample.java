package com.sergeyvolkodav.synchronizedExample;

import java.util.concurrent.locks.ReentrantLock;

public class WaitNotifyExample {


    public static void main(String[] args) throws InterruptedException {
        IncorrectSynchronization.runExample();
    }
}

class IncorrectSynchronization {

    ReentrantLock lock = new ReentrantLock();




    Car car  = new Car(250);

    public void example() throws InterruptedException{



        Thread t1 = new Thread(()->{
            synchronized (car){
                try {
                    while(car.maxSpeed==250){
                        System.out.println("First thread about to sleep");
                        Thread.sleep(5000);
                        System.out.println("Woke up and about to invoke wait()");
                        car.wait(); //todo Note
                        System.out.println("This will print later!!");
                    }

                }catch (Exception exception){
                    System.out.println("Threw exception");
                }
            }
        });

        Thread t2 = new Thread(()->{
            synchronized (car){
                car.maxSpeed = 400;
                System.out.println("Max speed assignment done.");
                car.notify();   //todo note this is important, for the above thread to resume.

                //todo note this will execute BEFORE the waiting thread starts execution
                System.out.println("This will print before!!");
            }
        });

        t1.start();
        Thread.sleep(1000);
        t2.start();

        t1.join();
        t2.join();

        Thread t3 = new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("From thread 3");
        });
//        t3.setDaemon(true);
        t3.start();
//        t3.join();



        Thread t4 = new Thread(()->{
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception for Thread 4");
            }
        });

        t4.start();
        t4.interrupt();

    }

    public static void runExample() throws InterruptedException {
        IncorrectSynchronization incorrectSynchronization = new IncorrectSynchronization();
        incorrectSynchronization.example();
    }
}

class Car {
    int maxSpeed;
    public Car(int maxSpeed){
        this.maxSpeed = maxSpeed;
    }

}
