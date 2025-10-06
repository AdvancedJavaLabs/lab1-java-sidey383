package org.itmo.bfs;

import org.itmo.Graph;

import java.util.Collection;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

public class ForkJoinPoolBFS implements BreadthFirstSearch {

    @Override
    public void execute(Graph graph, int startVertex, Consumer<Integer> consumer) {
        final Consumer<Integer> finalConsumer;
        finalConsumer = Objects.requireNonNullElseGet(consumer, () -> (i) -> {});

        boolean[] visited = new boolean[graph.vertexCount()];

        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

        try (ForkJoinPool pool = new ForkJoinPool()) {

            visited[startVertex] = true;
            finalConsumer.accept(startVertex);
            queue.add(startVertex);

            while (!queue.isEmpty()) {
                ConcurrentLinkedQueue<Integer> newQueue = new ConcurrentLinkedQueue<>();
                pool.invoke(new BFSForkJoinTask(graph, visited, queue.spliterator(), newQueue, finalConsumer));
                queue = newQueue;
            }
        }
    }

    @Override
    public String description() {
        return "Fork join pool with recursive task";
    }

    private static final Long SERIAL_SIZE = 100L;

    private static class BFSForkJoinTask extends RecursiveTask<Collection<Integer>> {

        private final Graph graph;
        private final boolean[] visited;
        private final Spliterator<Integer> currentNodes;
        private final Collection<Integer> collector;
        private final Consumer<Integer> consumer;

        public BFSForkJoinTask(Graph graph, boolean[] visited, Spliterator<Integer> currentNodes, Collection<Integer> collector, Consumer<Integer> consumer) {
            this.graph = graph;
            this.visited = visited;
            this.currentNodes = currentNodes;
            this.collector = collector;
            this.consumer = consumer;
        }

        @Override
        protected Collection<Integer> compute() {
            if (currentNodes.estimateSize() < SERIAL_SIZE) {
                syncCompute();
                return collector;
            }
            Spliterator<Integer> splitCurrentNodes = currentNodes.trySplit();
            if (splitCurrentNodes != null) {
                BFSForkJoinTask task1 = new BFSForkJoinTask(graph, visited, currentNodes, collector, consumer);
                BFSForkJoinTask task2 = new BFSForkJoinTask(graph, visited, splitCurrentNodes, collector, consumer);
                task1.fork();
                task2.fork();
                task1.join();
                task2.join();
            } else {
                syncCompute();
            }
            return collector;
        }

        private void syncCompute() {
            currentNodes.forEachRemaining(node -> graph.edgeList(node)
                    .forEach(newNode -> {
                                if (!visited[newNode]) {
                                    visited[newNode] = true;
                                    consumer.accept(newNode);
                                    collector.add(newNode);
                                }
                            }
                    )
            );
        }

    }

}
