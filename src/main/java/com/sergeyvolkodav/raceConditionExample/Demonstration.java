package com.sergeyvolkodav.raceConditionExample;

import java.util.HashMap;
import java.util.Random;

public class Demonstration {
    public static void main(String args[]) throws InterruptedException {
        RaceCondition.runTest();
    }
}
class RaceCondition {
    int randInt;
    Random random = new Random(System.currentTimeMillis()); //NOTE its taking current time in consideration
    static HashMap<Integer,Integer>hm = new HashMap<>();
    void printer() {
        int i = 1000000;
        while (i != 0) {

            //To make thread safe add  synchronized(this)  here before reading shared variable randInt

//            synchronized (this) {
                if (randInt % 5 == 0) {
                    if (randInt / 5 != 0) {
                        // IF not synchronized then, after condition 21 and 22 are satisfied,
                        // randInt will be modified by the modifier thread

                        //In case if it's synchronized then, modifier thread might not get a chance
                        //to update randInt and the same randInt value might get printed by printer thread multiple times.
                        hm.put(randInt,1);    //shared variable accessed by Thread 1
                    }
                }
                i--;
//            }
        }
    }


    void modifier() {
        int i = 1000000;
        while (i != 0) {
            //To make thread safe add  synchronized(this)  here before updating shared variable randInt

//            synchronized (this) {
                randInt = random.nextInt(1000);   //shared variable updated by Thread 2
                i--;
//            }
        }
    }


    public static void runTest() throws InterruptedException {
        final RaceCondition rc = new RaceCondition();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                rc.printer();
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                rc.modifier();
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        boolean invalid = false;
        for (int key : hm.keySet()) {
            if (key % 5 != 0) {
                invalid = true;
                System.out.println("Invalid Key " + key);
            }
        }
        if(!invalid){
            System.out.println("No invalid entries!!");
        }
    }
}

