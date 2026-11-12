import java.util.Arrays;

/**
 * Dijkstra with a plain array. The next node is found by scanning all nodes.
 * Time: O(V^2)   Space: O(V + E)
 * Used to show why the heap version is faster on sparse graphs.
 */
public class DijkstraArray {

    public static PathResult run(Graph g, int source) {
        int n = g.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        boolean[] done = new boolean[n];
        Arrays.fill(dist, PathResult.INF);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        for (int round = 0; round < n; round++) {
            // find the unfinished node with the smallest distance: O(V)
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!done[i] && dist[i] != PathResult.INF && (u == -1 || dist[i] < dist[u])) u = i;
            }
            if (u == -1) break;             // remaining nodes are unreachable
            done[u] = true;

            for (int[] edge : g.neighbours(u)) {
                int v = edge[0];
                int newDist = dist[u] + edge[1];
                if (!done[v] && newDist < dist[v]) {
                    dist[v] = newDist;
                    parent[v] = u;
                }
            }
        }
        return new PathResult(dist, parent);
    }
}
