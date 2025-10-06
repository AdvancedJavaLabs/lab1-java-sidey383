package org.itmo;

import org.itmo.bfs.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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

    public static Stream<Arguments> allVertexVisitedText() {
        RandomGraphGenerator generator = new RandomGraphGenerator();
        Random random = new Random(42);
        return Stream.of(
                generator.generateGraph(random, 10_000, 60_000),
                generator.generateGraph(random, 20_000, 30_000),
                generator.generateGraph(random, 10_000, 100_000)
        ).flatMap(graph -> Stream.of(
                        new SequentialBFS(),
                        new ParallelStreamBFS(),
                        new ForkJoinPoolBFS(),
                        new FixedThreadPoolBFS(4),
                        new FixedThreadPoolBFS(8),
                        new FixedThreadPoolBFS(16),
                        new ForkJoinPoolExecutorBFS(),
                        new VirtualThreadPoolBFS()
                ).map(algorithm -> Arguments.arguments(graph, algorithm))
        );
    }

    @MethodSource
    @ParameterizedTest
    public void allVertexVisitedText(Graph graph, BreadthFirstSearch algorithm) {
        ConcurrentLinkedQueue<Integer> visitedElements = new ConcurrentLinkedQueue<>();
        algorithm.execute(graph, 0, visitedElements::add);
        assertThat(visitedElements)
                .containsExactlyInAnyOrderElementsOf(
                        IntStream.range(0, graph.vertexCount()).boxed().toList()
                );
    }


    public static Stream<Arguments> isCorrectOrderTest() {
        RandomGraphGenerator generator = new RandomGraphGenerator();
        Random random = new Random(42);
        return Stream.of(
                generator.generateGraph(random, 1_000, 6_000),
                generator.generateGraph(random, 2_000, 3_000),
                generator.generateGraph(random, 1_000, 10_000)
        ).flatMap(graph -> Stream.of(
                        new SequentialBFS(),
                        new ParallelStreamBFS(),
                        new ForkJoinPoolBFS(),
                        new FixedThreadPoolBFS(4),
                        new FixedThreadPoolBFS(8),
                        new FixedThreadPoolBFS(16),
                        new ForkJoinPoolExecutorBFS(),
                        new VirtualThreadPoolBFS()
                ).map(algorithm -> Arguments.arguments(graph, generator.getGraphSlices(graph, 0), algorithm))
        );
    }


    @MethodSource
    @ParameterizedTest
    public void isCorrectOrderTest(Graph graph, List<Set<Integer>> nodeSlices, BreadthFirstSearch algorithm) {
        AtomicInteger sliceNum = new AtomicInteger();
        AtomicReference<Set<Integer>> currentSlice = new AtomicReference<>();
        currentSlice.set(Collections.synchronizedSet(new HashSet<>(nodeSlices.get(sliceNum.getAndIncrement()))));
        AtomicBoolean isFail = new AtomicBoolean(false);
        algorithm.execute(graph, 0, (node) -> {
            Set<Integer> s = currentSlice.get();
            // When can't found node in current slice - make synchronization
            if (!s.remove(node)) {
                synchronized (this) {
                    if (!s.isEmpty()) {
                        // Can't found node in slice
                        isFail.set(true);
                        return;
                    }
                    // Current slice is empty, change slice

                    // When somebody already change slice
                    if (currentSlice.get() == s) {
                        // Change slice
                        if (sliceNum.get() < nodeSlices.size()) {
                            currentSlice.set(Collections.synchronizedSet(new HashSet<>(nodeSlices.get(sliceNum.getAndIncrement()))));
                        } else {
                            // No more slices
                            isFail.set(true);
                        }
                    }
                    if (!currentSlice.get().remove(node)) {
                        //Can't found value in new slice
                        isFail.set(true);
                    }
                }
            }
        });
        assertThat(isFail.get()).isEqualTo(false);
        assertThat(sliceNum.get()).isEqualTo(nodeSlices.size());
        assertThat(currentSlice.get()).isEmpty();
    }

}
