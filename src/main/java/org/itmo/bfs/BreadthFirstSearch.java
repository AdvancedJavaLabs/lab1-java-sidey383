package org.itmo.bfs;

import org.itmo.Graph;

import java.util.function.Consumer;

public interface BreadthFirstSearch {

    void execute(Graph graph, int startVertex, Consumer<Integer> consumer);

    String description();

}
