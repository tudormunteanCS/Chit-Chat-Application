package domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    private int V;
    private List<List<Long>> adj;

    public Graph(int v) {
        V = v;
        adj = new ArrayList<>(V + 1);
        for (int i = 0; i <= V; i++) {
            adj.add(new LinkedList<>());
        }
    }

    public void addEdge(int v, Long w) {
        adj.get(v).add(w);
        //adj.get(w).add(v); // Pentru grafuri neorientate, adăugăm și muchia inversă.
    }

    public int countConnectedComponents() {
        boolean[] visited = new boolean[V + 1];
        int count = 0;

        for (int i = 1; i <= V; i++) {
            if (!visited[i]) {
                dfs(i, visited);
                count++;
            }
        }

        return count;
    }

    private void dfs(int v, boolean[] visited) {
        visited[v] = true;
        for (long neighbor : adj.get(v)) {
            if (!visited[(int) neighbor]) {
                dfs((int) neighbor, visited);
            }
        }
    }
}
