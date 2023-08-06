package com.sergeyvolkodav.taskScheduler.Demo;

import com.sergeyvolkodav.taskScheduler.interfaces.SchedulerService;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class SchedulerDemo {
    static long startTimeinSecs;
    public static void main(String[] args) {
        SchedulerService schedulerService = SchedulerService.getInstance();
        System.out.println("Demo started ");
        startTimeinSecs = System.currentTimeMillis()/1000;

        schedulerService.schedule(()->{
            System.out.println("This is from One Time Job, at " + getSecondsFromStart(System.currentTimeMillis()));
        },2, TimeUnit.SECONDS);

        schedulerService.scheduleWithFixedRate(()->{
            System.out.println("This is from Scheduled Fixed Rate Job, at " + getSecondsFromStart(System.currentTimeMillis()));
        },2,4,  TimeUnit.SECONDS);

        schedulerService.scheduleWithFixedDelay(()->{
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("This is from Scheduled Fixed Delay Job, at " + getSecondsFromStart(System.currentTimeMillis()));
        },2,4,  TimeUnit.SECONDS);
    }

    public static String getSecondsFromStart(long currentTimeinMillis){
        return  (currentTimeinMillis/1000) - startTimeinSecs + " secs";
    }
}
