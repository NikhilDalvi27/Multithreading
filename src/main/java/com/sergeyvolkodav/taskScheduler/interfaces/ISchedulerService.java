package com.sergeyvolkodav.taskScheduler.interfaces;

import java.util.concurrent.TimeUnit;

public interface ISchedulerService {

    public void schedule(Runnable task, long initialDelay, TimeUnit unit);

    /**
     * schedules task again after time=recurringDelay, without waiting for the initial submitted task to be completed
     * 1st run = initialDelay
     * 2nd run = recurringDelay
     * 3rd run = 2*recurringDelay
     */
    public void scheduleWithFixedRate(Runnable task, long initialDelay, long recurringDelay, TimeUnit unit);

    /**
     * schedules task again after time=recurringDelay AFTER the initial submitted task is completed
     * 1st run = initialDelay
     * 2nd run = time for 1st task to complete + recurringDelay
     * 3rd run = time for 2nd task to complete + recurringDelay
     */
    public void scheduleWithFixedDelay(Runnable task, long initialDelay, long recurringDelay, TimeUnit unit);
}
