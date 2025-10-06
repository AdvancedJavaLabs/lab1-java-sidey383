package org.itmo.bfs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadPoolBFS extends ExecutorServiceBFS {

    private final ExecutorService executorService;

    public VirtualThreadPoolBFS() {
        super();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public VirtualThreadPoolBFS(int serialSize) {
        super(serialSize);
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public String description() {
        return "Virtual thread pool";
    }
}
