package org.itmo.bfs;

import org.itmo.Graph;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

public class SequentialBFS implements BreadthFirstSearch {
    @Override
    public void execute(Graph graph, int startVertex, Consumer<Integer> consumer) {
        final Consumer<Integer> finalConsumer;
        finalConsumer = Objects.requireNonNullElseGet(consumer, () -> (i) -> {});
        boolean[] visited = new boolean[graph.vertexCount()];

        LinkedList<Integer> queue = new LinkedList<>();

        visited[startVertex] = true;
        queue.add(startVertex);
        finalConsumer.accept(startVertex);

        while (!queue.isEmpty()) {
            startVertex = queue.poll();

            for (int n : graph.edgeList(startVertex)) {
                if (!visited[n]) {
                    visited[n] = true;
                    finalConsumer.accept(n);
                    queue.add(n);
                }
            }
        }
    }

    @Override
    public String description() {
        return "Sequential";
    }
}
