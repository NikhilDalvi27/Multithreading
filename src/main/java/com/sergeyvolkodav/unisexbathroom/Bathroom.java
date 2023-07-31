package com.sergeyvolkodav.unisexbathroom;

import java.util.concurrent.Semaphore;

public class Bathroom {

    private Gender useBy = Gender.NONE;
    private int currentEmployees = 0;
    Semaphore employeeSemaphore = new Semaphore(3); /** to restrict only to 3 employees **/

    public void maleUseBathroom(String name) throws InterruptedException {

        //todo note, this synchronized block is to guard
        // useBy and emps variable which can be read and written to, by multiple threads
        synchronized (this) {
            while (useBy.equals(Gender.FEMALE)) {
                wait();
            }

            //todo note this is to restrict access to 3 employees only
            employeeSemaphore.acquire();


            useBy = Gender.MALE;
            currentEmployees++;
        }

        //todo note, the below 2 statements
        // should be executed in parallel
        // without the synchronized block
        useBathroom(Gender.MALE);

        employeeSemaphore.release();


        //todo note, this synchronized block is to guard
        // useBy and emps variable which can be read and written to, by multiple threads
        synchronized (this) {
            currentEmployees--;
            if (currentEmployees == 0) {
                useBy = Gender.NONE;
            }
            notifyAll(); /** Note this is IMP in case the Gender becomes none **/
        }
    }

    public void femaleUseBathroom(String name) throws InterruptedException {

        synchronized (this) {

            while (useBy.equals(Gender.MALE)) {
                wait();
            }
            employeeSemaphore.acquire();
            useBy = Gender.FEMALE;
            currentEmployees++;
        }

        useBathroom(Gender.FEMALE);

        employeeSemaphore.release();

        synchronized (this) {
            currentEmployees--;
            if (currentEmployees == 0) {
                useBy = Gender.NONE;
            }
            notifyAll();
        }
    }

    private void useBathroom(Gender gender) throws InterruptedException {
        System.out.println(gender.name() + " using bathroom. Current employees in bathroom = " + currentEmployees);
        Thread.sleep(3500);
        System.out.println("Done with bathroom");
    }
}
