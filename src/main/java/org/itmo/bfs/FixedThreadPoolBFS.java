package org.itmo.bfs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadPoolBFS extends ExecutorServiceBFS {

    private final ExecutorService executorService;
    private final int threadCount;

    public FixedThreadPoolBFS() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public FixedThreadPoolBFS(int threadCount) {
        super();
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    public FixedThreadPoolBFS(int serialSize, int threadCount) {
        super(serialSize);
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }


    @Override
    public String description() {
        return "Fixed thread pool with %d threads".formatted(threadCount);
    }
}
