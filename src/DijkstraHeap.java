import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Dijkstra with a binary min-heap (PriorityQueue).
 * Time: O((V + E) log V)   Space: O(V + E)
 */
public class DijkstraHeap {

    public static PathResult run(Graph g, int source) {
        int n = g.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        boolean[] done = new boolean[n];
        Arrays.fill(dist, PathResult.INF);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        // heap entries: {node, distance when it was pushed}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.add(new int[]{source, 0});

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int u = top[0];
            if (done[u]) continue;          // old entry, a shorter one was already processed
            done[u] = true;                 // greedy choice: u's distance is now final

            for (int[] edge : g.neighbours(u)) {
                int v = edge[0];
                int newDist = dist[u] + edge[1];
                if (!done[v] && newDist < dist[v]) {   // relaxation
                    dist[v] = newDist;
                    parent[v] = u;
                    pq.add(new int[]{v, newDist});
                }
            }
        }
        return new PathResult(dist, parent);
    }
}
