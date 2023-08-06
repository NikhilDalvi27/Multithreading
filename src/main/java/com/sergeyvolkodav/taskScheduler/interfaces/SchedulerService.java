package com.sergeyvolkodav.taskScheduler.interfaces;

import com.sergeyvolkodav.taskScheduler.entity.Job;
import com.sergeyvolkodav.taskScheduler.entity.OneTimeJob;
import com.sergeyvolkodav.taskScheduler.entity.RecurringJobFixedDelay;
import com.sergeyvolkodav.taskScheduler.entity.RecurringJobFixedRate;

import java.util.Calendar;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerService implements ISchedulerService {
    private static final SchedulerService schedulerService = new SchedulerService();
    private PriorityQueue<Job> jobPriorityQueue;
    private Lock queueLock;
    private Condition entryAdded;


    private SchedulerService() {
        jobPriorityQueue = new PriorityQueue<>();
        queueLock = new ReentrantLock();
        entryAdded = queueLock.newCondition();
        //Start JobExecutorThread here
        Thread jobExecutor = new Thread(new JobExecutor(jobPriorityQueue,queueLock,entryAdded));

        // todo note don't set it as daemon thread,
        //  since main thread might get completed
        //  before processing all the task present inside queue
        // jobExecutor.setDaemon(true);
        jobExecutor.start();
    }

    public static SchedulerService getInstance() {
        return schedulerService;
    }

    @Override
    public void schedule(Runnable task, long initialDelay, TimeUnit unit) {
        Date runDate = new Date(Calendar.getInstance().getTime().getTime() + unit.toMillis(initialDelay));
        Job job = new OneTimeJob(UUID.randomUUID(), task, runDate, unit);
        addToJobQueue(job);
    }

    @Override
    public void scheduleWithFixedRate(Runnable task, long initialDelay, long recurringDelay, TimeUnit unit) {
        Date runDate = new Date(Calendar.getInstance().getTime().getTime() + unit.toMillis(initialDelay));
        Job job = new RecurringJobFixedRate(UUID.randomUUID(), task, runDate, recurringDelay, unit,this);
        addToJobQueue(job);
    }

    @Override
    public void scheduleWithFixedDelay(Runnable task, long initialDelay, long recurringDelay, TimeUnit unit) {

        Date runDate = new Date(Calendar.getInstance().getTime().getTime() + unit.toMillis(initialDelay));
        Job job = new RecurringJobFixedDelay(UUID.randomUUID(), task, runDate, recurringDelay, unit,this);
        addToJobQueue(job);
    }

    public void addToJobQueue(Job job) {
        queueLock.lock();
        try {
            jobPriorityQueue.offer(job);
            entryAdded.signal();
        } finally {
            queueLock.unlock();
        }
    }
}
