package com.sergeyvolkodav.taskScheduler.entity;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public  abstract class Job implements Runnable,Comparable<Job> {

    protected UUID uuid;
    protected Runnable task;

    //todo note, runDate parameter is vimp,
    // bcoz that's how the job are arranged in the jobQueue
    // and we only execute the job who's runDate is now.
    protected Date runDate;


    protected TimeUnit unit;



    public Job(UUID uuid, Runnable task, Date runDate, TimeUnit unit) {
        this.uuid = uuid;
        this.task = task;
        this.runDate = runDate;
        this.unit = unit;
    }

    public Date getRunDate() {
        return runDate;
    }

    @Override
    public int compareTo(Job job) {
        return this.runDate.compareTo(job.runDate);
    }
}
