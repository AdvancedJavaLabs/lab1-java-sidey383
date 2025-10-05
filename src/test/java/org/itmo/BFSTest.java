package org.itmo;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.function.BiConsumer;

public class BFSTest {

    @Test
    public void bfsTest() throws IOException {
        int[] sizes = new int[]{10, 100, 1000, 10_000, 10_000, 50_000, 100_000, 1_000_000, 2_000_000};
        int[] connections = new int[]{50, 500, 5000, 50_000, 100_000, 1_000_000, 1_000_000, 10_000_000, 10_000_000};
        Random r = new Random(42);
        try (FileWriter fw = new FileWriter("tmp/results.txt")) {
            for (int i = 0; i < sizes.length; i++) {
                System.out.println("--------------------------");
                System.out.println("Generating graph of size " + sizes[i] + " ...wait");
                Graph g = new RandomGraphGenerator().generateGraph(r, sizes[i], connections[i]);
                System.out.println("Generation completed!\nStarting bfs");
                long serialTime = executeAndGetTime(g, Graph::bfs);
                long parallelStreamTime = executeAndGetTime(g, Graph::parallelStreamBfs);
                long forkJoinPoolTime = executeAndGetTime(g, Graph::forkJoinPoolBfs);
                long fixedThreadPoolTime = executeAndGetTime(g, Graph::fixedThreadPoolBfs);
                long forkJoinPoolExecutorTime = executeAndGetTime(g, Graph::forkJoinPoolExecutorBfs);
                long virtualPoolExecutorTime = executeAndGetTime(g, Graph::virtualPoolExecutorBfs);
                fw.append("Times for " + sizes[i] + " vertices and " + connections[i] + " connections: ");
                fw.append("\nSerial: " + serialTime);
                fw.append("\nParallel Stream: " + parallelStreamTime);
                fw.append("\nParallel ForkJoinPool: " + forkJoinPoolTime);
                fw.append("\nFixed Thread Pool: " + fixedThreadPoolTime);
                fw.append("\nFork join pool executor: " + forkJoinPoolExecutorTime);
                fw.append("\nVirtual pool executor: " + virtualPoolExecutorTime);
                fw.append("\n--------\n");
            }
            fw.flush();
        }
    }


    private long executeAndGetTime(Graph g, BiConsumer<Graph, Integer> consumer) {
        long startTime = System.currentTimeMillis();
        consumer.accept(g, 0);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

}
