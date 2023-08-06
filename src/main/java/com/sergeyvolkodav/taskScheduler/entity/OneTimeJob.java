package com.sergeyvolkodav.taskScheduler.entity;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OneTimeJob extends Job{

    ExecutorService executorService;
    public OneTimeJob(UUID uuid, Runnable task, Date runDate, TimeUnit unit) {
        super(uuid, task, runDate, unit);
        executorService = Executors.newFixedThreadPool(4);
    }

    @Override
    public void run() {
        executorService.submit(task);
    }
}
