package org.itmo;

import org.itmo.bfs.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class VisitingTest {

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

}
