package org.itmo.bfs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinPoolExecutorBFS extends ExecutorServiceBFS {

    private final ExecutorService executorService;

    public ForkJoinPoolExecutorBFS() {
        super();
        this.executorService = new ForkJoinPool();
    }

    public ForkJoinPoolExecutorBFS(int serialSize) {
        super(serialSize);
        this.executorService = new ForkJoinPool();
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public String description() {
        return "Fork join pool as executor service";
    }
}
