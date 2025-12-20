package com.example;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionService implements AutoCloseable {
    private final ExecutorService executor;

    public ExecutionService() {

        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }


    public void execute(Runnable task) {
        if (!executor.isShutdown()) {
            executor.execute(task);
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}