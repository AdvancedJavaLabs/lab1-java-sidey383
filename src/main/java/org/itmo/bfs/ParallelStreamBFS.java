package org.itmo.bfs;

import org.itmo.Graph;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ParallelStreamBFS implements BreadthFirstSearch {

    @Override
    public void execute(Graph graph, int startVertex, Consumer<Integer> consumer) {
        final Consumer<Integer> finalConsumer;
        finalConsumer = Objects.requireNonNullElseGet(consumer, () -> (i) -> {});

        boolean[] visited = new boolean[graph.vertexCount()];

        ConcurrentLinkedQueue<Integer> currentQueue = new ConcurrentLinkedQueue<>();

        visited[startVertex] = true;
        finalConsumer.accept(startVertex);
        currentQueue.add(startVertex);

        while (!currentQueue.isEmpty()) {
            ConcurrentLinkedQueue<Integer> finalNewQueue = new ConcurrentLinkedQueue<>();
            currentQueue.stream().parallel()
                    .forEach(node -> graph.edgeList(node).stream()
                            .filter(nextNode -> !visited[nextNode])
                            .forEach(nextNode -> {
                                visited[nextNode] = true;
                                finalConsumer.accept(nextNode);
                                finalNewQueue.add(nextNode);
                            }));
            currentQueue = finalNewQueue;
        }
    }

    @Override
    public String description() {
        return "Parallelism with stream parallel";
    }
}
