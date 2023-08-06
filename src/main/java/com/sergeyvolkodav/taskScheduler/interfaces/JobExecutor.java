package com.sergeyvolkodav.taskScheduler.interfaces;

import com.sergeyvolkodav.taskScheduler.entity.Job;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

//todo this is a task since, it needs to be executed in background in a separate thread.
public class JobExecutor implements Runnable {
    private PriorityQueue<Job> jobPriorityQueue;
    private Lock queueLock;
    private Condition entryAdded;

    ExecutorService executorService;

    public JobExecutor(PriorityQueue<Job> jobPriorityQueue, Lock queueLock, Condition entryAdded) {
        this.jobPriorityQueue = jobPriorityQueue;
        this.queueLock = queueLock;
        this.entryAdded = entryAdded;
        executorService = Executors.newFixedThreadPool(4);
    }

    @Override
    public void run() {
        while (true) {
            queueLock.lock();
            try {
                if (!jobPriorityQueue.isEmpty()) {
                    Job job = jobPriorityQueue.peek();
                    Date currentDate = new Date(System.currentTimeMillis());
                    if (currentDate.compareTo(job.getRunDate()) > 0) {
                        jobPriorityQueue.poll();
                        executorService.submit(job);
                    }
                } else {
                    try {
                        entryAdded.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                queueLock.unlock();
            }
        }
    }
}
