package org.itmo;

import org.itmo.bfs.*;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class BFSTest {

    @Test
    public void bfsTest() throws IOException {
        int[] sizes = new int[]{10, 100, 1000, 10_000, 10_000, 50_000, 100_000, 1_000_000, 2_000_000, 2_000_000};
        int[] connections = new int[]{50, 500, 5000, 50_000, 100_000, 1_000_000, 1_000_000, 10_000_000, 10_000_000, 100_000_000};
        Random r = new Random(42);
        Collection<BreadthFirstSearch> bfs = List.of(
                new SequentialBFS(),
                new ParallelStreamBFS(),
                new ForkJoinPoolBFS(),
                new FixedThreadPoolBFS(4),
                new FixedThreadPoolBFS(8),
                new FixedThreadPoolBFS(16),
                new ForkJoinPoolExecutorBFS(),
                new VirtualThreadPoolBFS()
        );
        try (FileWriter fw = new FileWriter("tmp/results.txt")) {
            for (int i = 0; i < sizes.length; i++) {
                System.out.println("--------------------------");
                System.out.println("Generating graph of size " + sizes[i] + " ...wait");
                Graph g = new RandomGraphGenerator().generateGraph(r, sizes[i], connections[i]);
                System.out.println("Generation completed!\nStarting bfs");

                fw.append("Times for ")
                        .append(String.valueOf(sizes[i]))
                        .append(" vertices and ")
                        .append(String.valueOf(connections[i]))
                        .append(" connections: ")
                        .append('\n');
                for (BreadthFirstSearch algorithm : bfs) {
                    long time = executeAndGetTime(g, algorithm);
                    fw.append(algorithm.description())
                            .append(": ")
                            .append(String.valueOf(time))
                            .append('\n');
                }
                fw.append("--------\n");
            }
            fw.flush();
        }
    }


    private long executeAndGetTime(Graph g, BreadthFirstSearch algorithm) {
        long startTime = System.currentTimeMillis();
        algorithm.execute(g, 0, null);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

}
