package org.itmo;

import java.util.*;

public class Graph {
    private final int V;
    private final ArrayList<Integer>[] adjList;

    public int vertexCount() {
        return V;
    }

    public List<Integer> edgeList(int vertex) {
        return adjList[vertex];
    }

    Graph(int vertices) {
        this.V = vertices;
        adjList = new ArrayList[vertices];
        for (int i = 0; i < vertices; ++i) {
            adjList[i] = new ArrayList<>();
        }
    }

    void addEdge(int src, int dest) {
        if (!adjList[src].contains(dest)) {
            adjList[src].add(dest);
        }
    }

}
