package com.sergeyvolkodav.taskScheduler.entity;

import com.sergeyvolkodav.taskScheduler.interfaces.SchedulerService;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RecurringJobFixedRate extends Job {
    long recurringDelay;

    //todo we need this to schedule the same job again
    SchedulerService schedulerService;

    public RecurringJobFixedRate(UUID uuid, Runnable task, Date runDate, long recurringDelay, TimeUnit unit, SchedulerService schedulerService) {
        super(uuid, task, runDate, unit);
        this.recurringDelay = recurringDelay;
        this.schedulerService = schedulerService;
    }

    @Override
    public void run() {

        Thread thread = new Thread(task);
        thread.start();

        //todo we cannot do  Thread.sleep(this.recurringDelay); here,
        // bcoz there can be a new job added
        // and the ordering of jobs might change
        this.runDate =  new Date(Calendar.getInstance().getTime().getTime() + unit.toMillis(recurringDelay));
        schedulerService.addToJobQueue(this);
    }
}
