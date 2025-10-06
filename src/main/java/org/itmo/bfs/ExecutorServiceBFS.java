package org.itmo.bfs;

import org.itmo.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public abstract class ExecutorServiceBFS implements BreadthFirstSearch {

    private final int serialSize;

    public ExecutorServiceBFS() {
        this(100);
    }

    public ExecutorServiceBFS(int serialSize) {
        this.serialSize = serialSize;
    }

    public abstract ExecutorService getExecutorService();

    @Override
    public void execute(Graph graph, int startVertex, Consumer<Integer> consumer) {
        final Consumer<Integer> finalConsumer;
        finalConsumer = Objects.requireNonNullElseGet(consumer, () -> (i) -> {});

        boolean[] visited = new boolean[graph.vertexCount()];

        List<Integer> nodes = new ArrayList<>();


        visited[startVertex] = true;
        finalConsumer.accept(startVertex);
        nodes.add(startVertex);

        while (!nodes.isEmpty()) {

            int partCount = Math.max(1, Math.min(32, nodes.size() / serialSize));

            ConcurrentLinkedQueue<Integer> collector = new ConcurrentLinkedQueue<>();
            List<Integer> finalNodes = nodes;
            var callableList = IntStream.range(0, partCount).mapToObj((part) ->
                    (Callable<Collection<Integer>>) (() -> runExecutedTask(graph, visited, finalNodes, collector, part, partCount, finalConsumer))
            ).toList();

            try {
                getExecutorService().invokeAll(callableList);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            nodes = new ArrayList<>(collector);
        }
    }

    private <T extends Collection<Integer>> T runExecutedTask(Graph graph, boolean[] visited, List<Integer> nodes, T collector, int part, int totalPartCount, Consumer<Integer> consumer) {
        int start = (int) ((long) nodes.size() * part / totalPartCount);
        int end = (int) ((long) nodes.size() * (part + 1) / totalPartCount);
        for (int i = start; i < end; i++) {
            int node = nodes.get(i);
            graph.edgeList(node).forEach(newNode -> {
                        if (!visited[newNode]) {
                            visited[newNode] = true;
                            consumer.accept(newNode);
                            collector.add(newNode);
                        }
                    }
            );
        }
        return collector;
    }

}
